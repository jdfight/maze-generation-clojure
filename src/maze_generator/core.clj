(ns maze-generator.core
  (:gen-class))

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
        (recur (conj grid (create-cell i (quot i height) (mod i width) width height))
               (inc i))))))
        
;;==== Selecting and linking Cells ====;;

(defn get-cell-by-id
  "Get a cell by id from a vector of cells"
  [cells id]
  (first (filter (comp #{id} :id) cells)))

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

(defn all-neighbors
  "Gets all non-nil neighbors for a given cell"
  [cell]
  (filter #(not= nil %) [(:north cell) (:south cell) (:east cell) (:west cell)]))

(defn get-unvisited-cells
  [grid]
  (filter (comp empty? :links) grid))

;;==== Maze Generation Algorithms ====;;

(defn binary-tree
  "creates a binary tree grid"
  [width]
  (loop [grid (create-grid width)
         cell (first grid)
         i 0]
    (if (>= i (* width width))
        grid       
        (recur (link-cell-bidirectionally grid i (get-random-neighbor cell [(:north cell) (:east cell)]))
               (get-cell-by-id grid (inc i))
               (inc i)))))


;; This gave me a lot of trouble because there were some tricks I didn'trealize were possible
;; Notable things I learned :
;;  You can have  multiple recur forms within the loop
;;  You can embed lets within the loop - careful about the scope of let/loop/recur
(defn recursive-backtracker
  "creates a recursive backtrackcer grid"
  [grid start-cell]
    (loop [maze grid
           visited [start-cell]]
           (if (empty? visited)
             maze
             (let [neighbor (all-neighbors (get-cell-by-id maze (first visited)))
                   unvisited-neighbors (get-unvisited-neighbors maze neighbor)]
                  
               (if (empty? (get-unvisited-neighbors maze neighbor))
                 (recur maze (rest visited))
                 (let [link-cell (rand-nth unvisited-neighbors)]
                   (recur (link-cell-bidirectionally maze (first visited) link-cell)
                          (conj visited link-cell))))))))

        
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



      
