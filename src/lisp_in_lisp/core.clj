(ns lisp-in-lisp.core
  (:gen-class))

;; Nil evaluates to nil
(my-eval {} nil)

;; Numbers eval to themselves
(my-eval {} 3)

;; Symbols eval to their value in the environment
(my-eval {'x 10} 'x)

;; Functions eval to themselves
(= (my-eval {} +) +)

;; Function application
(my-eval {'+ +} '(+ 1 1))
(my-eval {'/ /} '(/ 20 4))
(my-eval {'+ +} '(+ (+ 1 10) 2))

;; If statements with nil test eval else
(my-eval {} '(if nil 0 42))

;; If statements with non-nil test eval then
(my-eval {'/ /} '(if nil "a" (/ 1 0)))

;; Empty do statement evals to nil
(my-eval {} '(do))

;; One-expression do evals to expression
(my-eval {} '(do 42))

;; Multi-expression do evals to last expression
(my-eval {} '(do 1 2 3))

;; Let statements add to environment
(my-eval {} '(let [x 5] x))

;; Ensure we're calling my-eval recursively
(my-eval {'+ +} '(do (+ 1 2)))

;; do expressions evaluate all forms
(my-eval {'deref deref 'reset! reset! 'a (atom nil) '+ +} '(do (reset! a 10) (+ (deref a) 75)))

;; simple
(defn my-eval [env exp]
  (cond
    (nil? exp) nil
    (number? exp) exp
    (string? exp) exp
    (symbol? exp) (env exp)
    (fn? exp) exp))

;; with functions
(defn my-eval [env exp]
  (cond
    (nil? exp) nil
    (number? exp) exp
    (string? exp) exp
    (symbol? exp) (env exp)
    (fn? exp) exp
    (seq? exp) 
    (let [[op & args] exp]
      (apply (my-eval env op) (map #(my-eval env %) args)))))

;; with if
(defn my-eval [env exp]
  (cond
    (nil? exp) nil
    (number? exp) exp
    (string? exp) exp
    (symbol? exp) (env exp)
    (fn? exp) exp
    (seq? exp) 
    (let [[op & args] exp]
      (cond
        (= op 'if)
        (let [[test then else] args]
          (if (not (nil? (my-eval env test))) (my-eval env then) (my-eval env else)))
        :else (apply (my-eval env op) (map #(my-eval env %) args))))))

;; with do
(defn my-eval [env exp]
  (cond
    (nil? exp) nil
    (number? exp) exp
    (string? exp) exp
    (symbol? exp) (env exp)
    (fn? exp) exp
    (seq? exp) 
    (let [[op & args] exp]
      (cond
        (= op 'if)
        (let [[test then else] args]
          (if (not (nil? (my-eval env test))) (my-eval env then) (my-eval env else)))
        (= op 'do)
        (when-not (empty? args)
          (loop [[head & tail] args]
            (if (empty? tail)
              (my-eval env head)
              (do
                (my-eval env head)
                (recur tail)))))
        :else (apply (my-eval env op) (map #(my-eval env %) args))))))

;; with let
(defn my-eval [env exp]
  (cond
    (nil? exp) nil
    (number? exp) exp
    (string? exp) exp
    (symbol? exp) (env exp)
    (fn? exp) exp
    (seq? exp) 
    (let [[op & args] exp]
      (cond
        (= op 'if)
        (let [[test then else] args]
          (if (not (nil? (my-eval env test))) (my-eval env then) (my-eval env else)))
        (= op 'do)
        (when-not (empty? args)
          (loop [[head & tail] args]
            (if (empty? tail)
              (my-eval env head)
              (do
                (my-eval env head)
                (recur tail)))))
        (= op 'let)
        (let [[[var val] & body] args]
          (my-eval (assoc env var val) (cons 'do body)))
        :else (apply (my-eval env op) (map #(my-eval env %) args))))))

