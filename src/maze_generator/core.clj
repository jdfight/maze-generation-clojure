(ns maze-generator.core
  (:require [clojure.data.json :as json]
            [ring.adapter.jetty   :refer [run-jetty]]
            [clojure.pprint       :refer [pprint]]
            [compojure.core       :refer [routes GET POST]]
            [compojure.route      :refer [not-found]]
            [ring.middleware.json :refer [wrap-json-response wrap-json-body]]
            [ring.util.response   :refer [response]])
  (:use [clojure.walk]
        [ring.middleware.params])
  (:gen-class
	:main true))
   

;;==== Creating Cells  ====;;

(def new-cell { :id 0 :row 0 :column 0 :north nil :south nil :east nil :west nil :links [] })

(defn cell-id
  "Returns a unique cell id based on the row and column"
  [row column width]
  (+ (* width row) column))

(defn get-north
  [r c  width]
  (if (> r 0)
    (cell-id (dec r) c width)
    nil))

(defn get-south
  [r c  width height]
  (if (< (inc r) height)
    (cell-id (inc r) c width)
    nil))

(defn get-east
  [r c width]
  (if (< c (dec width))
    (cell-id r (inc c) width) 
    nil))

(defn get-west
  [r c width]
  (if (> c 0)
    (cell-id r (dec c) width)
    nil))

(defn create-cell
  "Creates a  new cells used to populate an initial grid of cells"
  [id row column width height]
  (merge new-cell {:id id
                   :row row
                   :column column
                   :north (get-north row column width)
                   :south (get-south row column width height)
                   :east (get-east row column width)
                   :west (get-west row column width) })) 

(defn create-grid
  "Creates a grid consisting of cells. Can pass a single width for square grids or width and height for rectangular grids"
  ([width] (create-grid width width))
  ([width height]
   (loop [grid []
          i 0]
     (if (>= i (* width height))
       grid ;;(into (sorted-map) grid)  
       (recur (conj grid (create-cell i (quot i width) (mod i width) width height))
              (inc i))))))

;;==== Selecting and linking Cells ====;;

(defn get-cell-by-id
  "Get a cell by id from a vector of cells"
  [cells id]
  (first (filter (comp #{id} :id) cells)))

(defn remove-cell-by-id
  [cells id]
  (vec (remove #(= id (:id %)) cells)))

(defn link-cell-undirectionally
  "Links a cell with a neighboring cell unidirectionally"
  [cells cell-id link-cell-id]
  (if (or (= cell-id nil) (= link-cell-id nil))
    cells
    (assoc cells cell-id (merge (nth cells cell-id) {:links (conj (:links (nth cells cell-id)) link-cell-id)}))))

(defn link-cell-bidirectionally
  "Links a cell with a neighboring cell bidirectionally"
  [cells cell-id link-cell-id]
  (if (or (= cell-id nil) (= link-cell-id nil))
    cells
    (assoc (link-cell-undirectionally cells cell-id link-cell-id)
           link-cell-id 
           (merge (nth cells link-cell-id) {:links (conj (:links (nth cells link-cell-id)) cell-id)}))))

(defn get-random-neighbor
  [cell neighbors]
  (let[good-neighbors (filter #(not= nil %) neighbors)]
    (if (not-empty good-neighbors)
      (rand-nth good-neighbors)
      nil)))

(defn get-unvisited-neighbors
  [grid neighbors]
  (loop [neighbor (first neighbors)
         unvisited []
         remaining neighbors]
    (if (empty? remaining)
      (filter #(not= nil %) unvisited)
      (recur (first (rest remaining))
             (if (empty? (:links (get-cell-by-id grid neighbor)))
               (conj unvisited neighbor)
               unvisited)
             (rest remaining)))))

(defn get-unvisited-neighbor-ids
  [grid neighbors]
  (loop [neighbor (first neighbors)
         unvisited []
         remaining neighbors]
    (if (empty? remaining)
      (filter #(not= nil %) unvisited)
      (recur (first (rest remaining))
             (if (empty? (:links (get-cell-by-id grid neighbor)))
               (conj unvisited (:id neighbor))
               unvisited)
             (rest remaining)))))

(defn all-neighbors
  "Gets all non-nil neighbors for a given cell"
  [cell]
  (filter #(not= nil %) [(:north cell) (:south cell) (:east cell) (:west cell)]))

(defn get-unvisited-cells
  [grid]
  (filter (comp empty? :links) grid))

(defn get-visited-neighbors
  [maze cell]
  (loop [visited []
        neighbors (all-neighbors cell)]
    (if (empty? neighbors)
      visited
      (do
        (if (not-empty (:links (get-cell-by-id maze (first neighbors))))
          (recur (conj visited (first neighbors)) (rest neighbors))
          (recur visited (rest neighbors)))))))

;;==== Maze Generation Algorithms ====;;

;; Binary Tree:
(defn binary-tree
  "creates a binary tree grid"
  ([width] (binary-tree width width))
  ([width height]
   (loop [grid (create-grid width height)
          cell (first grid)
          i 0]
     (if (>= i (* width height))
       grid       
       (recur (link-cell-bidirectionally grid i (get-random-neighbor cell [(:north cell) (:east cell)]))
              (get-cell-by-id grid (inc i))
              (inc i))))))


;; Recursive Backtracker
;; This gave me a lot of trouble because there were some tricks I didn'trealize were possible
;; Notable things I learned :
;;  You can have  multiple recur forms within the loop
;;  You can embed lets within the loop - careful about the scope of let/loop/recur
(defn recursive-backtracker
  "creates a recursive backtrackcer grid"
  [width height]
  (loop [maze (create-grid width height)
         visited [(rand-int (* width height))]]
    (if (empty? visited)
      maze
      (let [neighbor (all-neighbors (get-cell-by-id maze (last visited)))
            unvisited-neighbors (get-unvisited-neighbors maze neighbor)]
        
        (if (empty? (get-unvisited-neighbors maze neighbor))
          (recur maze (pop visited))
          (let [link-cell (rand-nth unvisited-neighbors)]
            (recur (link-cell-bidirectionally maze (last visited) link-cell)
                   (conj visited link-cell))))))))


;;Aldous Broder
(defn aldous-broder
  "Randomly walk, linking cells until all cells have been visited"
  [width height]
  (loop [maze (create-grid width height)
         cell (rand-nth maze)
         neighbor (get-cell-by-id maze (rand-nth (all-neighbors cell)))
         unvisited (dec (count maze))]
    (if (< unvisited 1)
      maze
      (do
        (if (empty? (:links neighbor))
          (recur (link-cell-bidirectionally maze (:id cell) (:id neighbor))
                 neighbor
                 (get-cell-by-id maze (rand-nth (all-neighbors neighbor)))
                 (dec unvisited))
          (recur maze
                 neighbor
                 (get-cell-by-id maze (rand-nth (all-neighbors neighbor)))
                 unvisited))))))


;;Wilson's
(defn wilson
  "similar to Aldous Broder but keeps track of its path and erasing it as it traverses through the maze."
  [width height]
  (loop [maze (create-grid width height)
         cell (rand-nth maze)
         unvisited (remove-cell-by-id maze (:id cell))]
    ;; Local functions that will help traverse and link the path.
    (letfn [(wilson-path
              ;;Start at a cell and randomly walk unvisited neighbors, building up the path
              [unvisited wpcell path]
              (if (nil? (get-cell-by-id unvisited (:id wpcell)))
                path
                (do
                  (let [neighbor (get-cell-by-id maze (rand-nth (all-neighbors wpcell)))
                        pos (.indexOf path neighbor)]
                    (if (> pos -1)
                      (recur
                       unvisited
                       neighbor
                       (subvec path 0 (inc pos)))
                      (recur unvisited neighbor (conj path neighbor)))))))

            (wilson-path-linker
              ;;Traverse through the path and link cells to their neighbors
              [maze unvisited path]
              (if (< (count path) 2)
                {:m maze :u unvisited}
                (recur
                 (link-cell-bidirectionally maze (:id (first path)) (:id (second path)))
                 (remove-cell-by-id unvisited (:id (first path)))
                 (rest path))))]
      ;; End function definitions
      (if (empty? unvisited)
        maze
        (do
          (let [ncell (rand-nth unvisited)
                path (wilson-path unvisited ncell [ncell])
                linked (wilson-path-linker maze unvisited path)]
            (recur
             (:m linked)
             ncell
             (:u linked))))))))



;;Recursive walk.  This was a failed attempt at implementing REcursive backtracker - but it generates a valid maze and it is interesting.
(defn walk-to-neighbors
  "walk through cells and neighbors, linking unvisted neighbors"
  [maze cell neighbors]
  (loop [m maze
         c cell
         ns neighbors]
    (if (empty? (get-unvisited-cells m))
      m
      (do
           (let [neighbor (get-cell-by-id m (first ns))]
             (if (empty? (:links neighbor))
               (recur (link-cell-bidirectionally m (:id c) (:id neighbor))
                      neighbor
                      (shuffle (all-neighbors neighbor)))              
               (recur m neighbor (shuffle (all-neighbors neighbor))))))))) ;; got to random neighbors from the current one. 
                           


(defn recursive-walk
  "This doesn't actually achieve the effect of recursive backwalker - but I like the resulting mazes."
  [width height]
  (let [maze (create-grid width height)
        cell (rand-nth maze)
        neighbors (shuffle (all-neighbors cell))]
    ;;(println "start at" (str (:id cell)))
   (walk-to-neighbors maze cell neighbors)))



;; Hunt and Kill
(defn go-hunting
  "Hunt across the maze for cells to link with visited neighbors"
  [maze]
  (loop [m maze
         cells maze
         ncell nil]
      (if (empty? cells)
        {:maze m :cell ncell}
        (do
          (let [cell (first cells)
                visited-neighbors (get-visited-neighbors m cell)]
            (if (and (empty? (:links cell)) (some? (not-empty visited-neighbors)))
              (do (let [rand-neighbor (rand-nth visited-neighbors)]
              (recur (link-cell-bidirectionally m (:id cell) rand-neighbor) (rest cells) cell)))
              (recur m (rest cells) ncell)))))))
      
    
(defn hunt-and-kill
  "start at a random cell and link unvisited neighbors until reaching a cell with no unvisited neighbors.  Then hunt the entire maze to link already visited cells."
  [width height]
  (loop [maze (create-grid width height)
         current-cell (rand-nth maze)]
    (if (nil? current-cell)
      maze
      (do
        (let [unvisited-neighbors (shuffle (get-unvisited-neighbors maze (all-neighbors current-cell)))
              link-result (link-cell-bidirectionally maze (:id current-cell) (first unvisited-neighbors))]
              (if (some? (not-empty unvisited-neighbors))
                (recur link-result (get-cell-by-id link-result (first unvisited-neighbors)))
                (let [hunt (go-hunting maze)]
                  (recur (:maze hunt) (:cell hunt)))))))))
                


;;==== Printing Mazes to the Console ====;;

(defn cell-linked
  [cell side]
  (some #(= (side cell) %) (:links cell)))

(defn cell-top-to-string
  [cell]
  (if (cell-linked cell :east)
    "    "
    "   |"))

(defn cell-bottom-to-string
  [cell]
  (if (cell-linked cell :south)
    "   +"
    "---+"))

(defn cell-row-top-to-string
  [cells]
  (loop [result "|"
         remaining cells]
    (if (empty? remaining)
      (apply str (concat result "\n"))
      (recur (apply str (concat result (cell-top-to-string (first remaining))))
             (rest remaining)))))

(defn cell-row-bottom-to-string
  [cells]
  (loop [result "+"
         remaining cells]
    (if (empty? remaining)
      (apply str (concat result "\n"))
      (recur (apply str (concat result (cell-bottom-to-string (first remaining))))
             (rest remaining)))))


(defn maze-to-string
  [cells width]
  (loop [rest-cells cells
         row (take width rest-cells)
         output (apply str (concat "+" (repeat width "---+") "\n"))]
    (if (empty? rest-cells)
      output
      (recur (drop width rest-cells)
             (take width (drop width rest-cells))
             (apply str (concat output (cell-row-top-to-string row) (cell-row-bottom-to-string row)))))))

(defn print-maze
  [fn-maze width height]
  (println (maze-to-string (fn-maze width height) width)))


;; Output maze to JSON
(defn validate-maze-fn
  [name]
  (ns-resolve *ns* name))

(defn maze-title
  [maze-fn]
  (cond
    (= maze-fn aldous-broder) "Aldous Broder"
    (= maze-fn binary-tree) "Binary Tree"
    (= maze-fn wilson) "Wilson's"
    (= maze-fn hunt-and-kill) "Hunt and Kill"
    (= maze-fn recursive-backtracker) "Recursive Backtracker"
    (= maze-fn recursive-walk) "Recursive Walk"))

(defn name-to-maze-fn
  [name]
  (cond
    (= name "aldous-broder") aldous-broder
    (= name "binary-tree") binary-tree
    (= name "wilson") wilson
    (= name "hunt-and-kill") hunt-and-kill
    (= name "recursive-backtracker") recursive-backtracker
    (= name "recursive-walk") recursive-walk))
    
(defn json-maze
  [maze-fn width height]
  (let [maze (maze-fn width height)]
  (json/write-str {:title (maze-title maze-fn)
                   :width width
                   :height height
                   :maze maze
                   :to-string (maze-to-string maze width)})))
  
(defn random-maze-fn
  []
   (let [roll (rand-int 100)
        maze-fn
        (cond
          (< roll 16) aldous-broder
          (< roll 32) binary-tree
          (< roll 48) wilson
          (< roll 64) recursive-backtracker
          (< roll 80) hunt-and-kill
          (< roll 100) recursive-walk)]
     maze-fn))

(defn random-maze
  []
  (let [roll (rand-int 100)
        maze-fn (random-maze-fn) 
        width (rand-nth (range 10 20))
        height (rand-nth (range 10 20))]
    (maze-fn width height)))


(defn print-random-maze
  []
  (let [width (rand-nth (range 10 30))
        height (rand-nth (range 10 30))
        maze (random-maze-fn)]
    (print-maze maze width height)))

;; Web server
(defn maze-request-handler
  [request]
  (let [mapped-params (keywordize-keys (:params request))
        maze-fn (name-to-maze-fn (:name mapped-params))
        w (Integer/parseInt (:w mapped-params))
        h (Integer/parseInt (:h mapped-params))]
    (if (or (nil? maze-fn) (nil? (:w mapped-params)) (nil? (:h mapped-params)))
      nil
      (json-maze maze-fn w h))))


(def my-routes
  (routes 
   (GET  "/random-maze"  []     (response (json-maze (random-maze-fn) (rand-nth (range 10 24)) (rand-nth (range 10 24)))))
   (GET  "/maze"        request (response (maze-request-handler request)))
   (POST "/debug"       request (response (with-out-str (clojure.pprint/pprint request))))
   (not-found {:error "Not found"})))

(def app
  (-> my-routes
      wrap-params
      wrap-json-body
      wrap-json-response))

(defn -main
  []
  (run-jetty app {:port 3000}))


