package com.example.codigosbasicos.arbol;

// LOS MÉTODOS SE TRABAJARON CON DOBLE PARÁMETRO PARA PODER USARLOS EN UN SUBÁRBOL Y NO SIEMPRE USAR LA RAÍZ
/**BinaryTree representa un árbol binario de organización de objetos, siguiendo la norma de menor id a la izquierda y mayor id a la derecha. Contiene una raíz(root) que es el nodo principal del árbol.
 * NOTA: un árbol binario de búsqueda no debería estar en orden de llenado secuencial, es decir, no estará optimizado sino cuando esté armado sin seguir una secuencia (al azar). i.e. si hubieran nodos del 1-100 y quiero conseguir el 100, deberé recorrer 99 nodos antes en cambio si está al azar, quizás sea el primero o quizás el décimo.
 * @author Leo
 * @version 1.0
 * @since ApiLeo 1.0
 * @see <a href = "http://google.com" > Enlace a google.com </a>
 * @see com.example.codigosbasicos.arbol.BinaryNode
 */
public class BinaryTree {
    
    private BinaryNode root;
    
    /**Constructor que recibe un nodo y lo asigna a la raíz del árbol
     * @param n Nodo recibido
     */
    public BinaryTree(BinaryNode n) { root = n; }

    /**Consultar si el árbol está vacío o no
     * @return true si el árbol está vacío, falso en caso contrario
     */
    public boolean isEmptyTree() { return (root == null); }
	
    public BinaryNode getRoot() { return root; }

    public void setRoot(BinaryNode p) { root = p; }

    /**return true if a node with that id already exists in the binary tree... Notar que el árbol no tiene que estar ordenado
     * @param id id del nodo a buscar
     * @param aux recibe root/subroot del árbol en su llamado
     * @return true si contiene un nodo con tal identificador
     */
    public static boolean hasNode(int id, BinaryNode aux) {
	if (aux == null) return false; 
        if (aux.getId() == id) return true;
        else return ( hasNode(id,aux.getHijoIzq()) || hasNode(id,aux.getHijoDer()) );
    }

    /**return true if a node with that id already exists in the binary tree... Notar que el árbol TIENE que estar ordenado
     * @param id id del nodo a buscar
     * @param aux recibe root/subroot del árbol en su llamado
     * @return true si contiene un nodo con tal identificador
     */
    public static boolean hasNodeOrderedTree(int id, BinaryNode aux) {
        if (aux == null) return false;
	if (aux.getId() == id) return true;
	if(id > aux.getId()) return hasNodeOrderedTree(id,aux.getHijoDer());
        return hasNodeOrderedTree(id,aux.getHijoIzq());
    }
    
    /**return a BinaryNode if a node with that id already exists in the binary tree... Notar que el árbol TIENE que estar ordenado
     * @param id id del nodo a retornar
     * @param aux recibe root/subroot del árbol en su llamado
     * @return null si el nodo no existe, BinaryNode en caso de encontrarlo
     */
    public static BinaryNode getNodeOrderedTree(int id, BinaryNode aux) {
        if (aux == null) return null;
	if (aux.getId() == id) return aux;
	if(id > aux.getId()) return getNodeOrderedTree(id,aux.getHijoDer());
        return getNodeOrderedTree(id,aux.getHijoIzq());
    }
    
    // retorna el hijoIzq del nodo con identificador id, null sino tiene hijoIzq
    // retornar null no es garantía que el nodo de id exista, por ello debería chequearse antes
    public static BinaryNode findHijoIzq(int id,BinaryNode aux) {
	if (aux == null) return null;
	if (aux.getId() == id) return aux.getHijoIzq();
	if (id > aux.getId()) return findHijoIzq(id,aux.getHijoDer());
	return findHijoIzq(id,aux.getHijoIzq());
    }
	
    // retorna el hijoDer del nodo con identificador id, null sino tiene hijoDer
    // retornar null no es garantía que el nodo de id exista, por ello debería chequearse antes
    public static BinaryNode findHijoDer(int id,BinaryNode aux) {
        if (aux == null) return null;
	if (aux.getId() == id) return aux.getHijoDer();
	if (id > aux.getId()) return findHijoDer(id,aux.getHijoDer());
	return findHijoDer(id,aux.getHijoIzq());
    }

    public static int countNodes(BinaryNode aux) {
	if(aux == null) return 0;
        return (1 + countNodes(aux.getHijoIzq()) + countNodes(aux.getHijoDer()));
    }

    public static int countLeafs(BinaryNode aux) {
    	if (aux == null) return 0;
    	if (aux.isLeaf()) return 1;
    	return (countLeafs(aux.getHijoIzq()) + countLeafs(aux.getHijoDer()));
    }

    // insertar un nodo en el árbol siguiendo la regla de ordenamiento, id menor a la izquierda y mayor a la derecha
    // si el nodo ya existe, se sustituye su objeto interno, deberemos chequear antes de llamar a este método
    // si llamo al método enviándole aux como un nodo que no sea la raíz, es decir, un subárbol, podré repetir nodos en el árbol siempre que no se encuentren en el subárbol del aux enviado
    public void insertNode(BinaryNode n, BinaryNode aux) {
	if (root == null) root = n;
        else if (aux == null || n == null) ;// do nothing (pero lo usé para detener las siguientes comprobaciones que fallarían)
        else if (n.getId() == aux.getId()) aux.setObject(n.getObject());// sustituye el actual por el nuevo
        else if (n.getId() > aux.getId()) {
            if (aux.getHijoDer() == null) aux.setHijoDer(n);
            else insertNode(n,aux.getHijoDer());
        } else {
            if (aux.getHijoIzq() == null) aux.setHijoIzq(n);
            else insertNode(n,aux.getHijoIzq());
	}
    }
    
    /**ELIMINA UN NODO DEL ÁRBOL, SIGUIENDO LAS REGLAS DE ORDENACIÓN MAYOR A LA DER Y MENOR A LA IZQ... NO SE CHEQUEA QUE EL NODO EXISTA YA QUE SE REPETIRÍA LA BÚSQUEDA EN CADA ITERACIÓN SOBRE EL MÉTODO, DEBE HACERSE ANTES!... PUDO HABERSE HECHO UNA ELIMINACIÓN LÓGICA, COLOCANDO UN BOOLEAN EN EL NODO QUE INDIQUE SI ELIMINADO O NO, PERO LA FORMA QUE USAMOS REESTRUCTURA EL ÁRBOL.
     * @param id id del nodo a eliminar
     * @param aux root
     * @param padre null
     */
    public void removeNode(int id, BinaryNode aux, BinaryNode padre) {
	if (aux != null) {// check que inicialice con alguna root/subroot
            if(id < aux.getId()) {// si el nodo buscado está a la izquierda
                removeNode(id,aux.getHijoIzq(),aux);// en el próximo ciclo, padre apuntará al nodo actual
            } else if (id > aux.getId()) {// si el nodo buscado está a la derecha
		removeNode(id,aux.getHijoDer(),aux);
            } else {// si encontró el nodo a eliminar
		if (aux.isLeaf()) {
                    if (aux == root) root = null;
                    else if (id < padre.getId()) padre.setHijoIzq(null);
                    else if (id > padre.getId()) padre.setHijoDer(null);
		} else if (aux.getHijoIzq() != null) {// buscará un sustituto por la izquierda
                    BinaryNode mayorLeft = findHighestNode(aux.getHijoIzq());// encontramos el mayor de los nodos menores al que eliminaremos (mL)
                    removeSubTree(mayorLeft);// eliminamos el subárbol del nodo (mL)
                    if (aux.getHijoDer() != null) mayorLeft.setHijoDer(aux.getHijoDer());// si el eliminando tiene algo a su derecha, se la asignamos a mL
                    BinaryNode lowest = findLowestNode(mayorLeft);// encontramos, del subárbol de mL, el menor nodo (mLm)
                    lowest.setHijoIzq(aux.getHijoIzq());// le asignamos a mLm el subárbol a la izq del eliminando
                    if (aux == root) root = mayorLeft;// si el eliminando era la raíz, ahora mL es la nueva raíz
		} else if (aux.getHijoDer() != null) {// buscará un sustituto por la derecha
                    BinaryNode minorRight = findLowestNode(aux.getHijoDer());
                    removeSubTree(minorRight);
                    minorRight.setHijoIzq(aux.getHijoIzq());// ya sabemos que a la izq del eliminando no hay nada pq pasó por el if arriba
                    BinaryNode highest = findHighestNode(minorRight);
                    highest.setHijoDer(aux.getHijoDer());
                    if (aux == root) root = minorRight;
		}
            }
	}
    }
    
    // retorna el nodo con el mayor id
    public static BinaryNode findHighestNode(BinaryNode aux) {
        if (aux == null) return null;
        if (aux.getHijoDer() != null) return findHighestNode(aux.getHijoDer());
        return aux;
    }
    
    // retorna el nodo con el menor id
    public static BinaryNode findLowestNode(BinaryNode aux) {
        if (aux == null) return null;
        if (aux.getHijoIzq() != null) return findLowestNode(aux.getHijoIzq());
        return aux;
    }
    
    // dado un nodo como raíz de un subárbol, elimina todo el subárbol incluyendo esa raíz... debe verificarse que exista el nodo previamente
    public void removeSubTree(BinaryNode aux) {
        if (aux == root) root = null;
        else if (aux != null) {
            BinaryNode padre = getPadre(aux.getId(),root);
            if (aux.getId() < padre.getId()) padre.setHijoIzq(null);
            else padre.setHijoDer(null);
        }
    }
    
    // dado el id de un nodo (existente), retorna su padre... inicializar con padre = root o subroot
    // si el nodo buscado es la raíz/subraíz retorna null pq no puede saber el padre
    public static BinaryNode getPadre(int id, BinaryNode padre) {
        if (padre == null || padre.getId() == id) return null;
        if (id < padre.getId()) {
            if (padre.getHijoIzq().getId() == id) return padre;
            return getPadre(id,padre.getHijoIzq());
        } else {
            if (padre.getHijoDer().getId() == id) return padre;
            return getPadre(id,padre.getHijoDer());
        }
    }
    
    // dado el id de un nodo, elimina su hijo izquierdo del árbol, pero no todo el subárbol izq
    public void removeLeft(int id) {
        if (findHijoIzq(id, root) != null) removeNode(id, root, null);
    }
    
    // dado el id de un nodo, elimina su hijo derecho del árbol, pero no todo el subárbol der
    public void removeRight(int id) {
        if (findHijoDer(id, root) != null) removeNode(id, root, null); 
    }
    
    public static void preOrder(BinaryNode aux) {
	if (aux != null) {
            System.out.print(aux.getId()+" ");
            preOrder(aux.getHijoIzq());
            preOrder(aux.getHijoDer());
	}
    }
	
    public static void inOrder(BinaryNode aux) {
        if (aux != null) {
            inOrder(aux.getHijoIzq());
            System.out.print(aux.getId()+" ");
            inOrder(aux.getHijoDer());
	}
    }
    
    public static void postOrder(BinaryNode aux) {
	if(aux != null) {
            postOrder(aux.getHijoIzq());
            postOrder(aux.getHijoDer());
            System.out.print(aux.getId()+" ");
	}
    }
    
    // dado un nodo raíz/subraíz, devuelve la altura de ese árbol/subárbol... debe llamarse al método con cont = 1
    public static int getHeight(BinaryNode aux, int cont) {
	if (aux == null) return 0;
        if (aux.isLeaf()) return cont;
        if (aux.getHijoIzq() != null && aux.getHijoDer() != null) return Math.max( getHeight(aux.getHijoIzq(),cont+1), getHeight(aux.getHijoDer(),cont+1) );
        if (aux.getHijoIzq() != null) return getHeight(aux.getHijoIzq(),cont+1);
        return getHeight(aux.getHijoDer(),cont+1);
    }
    
    // dado el id de un nodo, retorna su nivel
    // llamar al método con aux = root y level = 0... retorna -1 si aux==null ó el nodo no existe
    public static int getNodeLevel(int id, BinaryNode aux, int level) {
    	if (aux == null) return -1;
    	if (aux.getId() == id) return level;
        if (id < aux.getId()) return getNodeLevel(id,aux.getHijoIzq(),level+1);
    	return getNodeLevel(id,aux.getHijoDer(),level+1);
    }
    
    // dado un level, visita/opera sobre los nodos de dicho level...se llama inicialmente con la root, el nivel a visitar y cont=0
    // i.e. si deseo visitar el level 2 de un subárbol, llamo al método con la subroot, level=2 y cont=0
    public static void visitLevel(BinaryNode aux, int level, int cont) {
    	if (aux != null) {
            if (cont == level) {
                System.out.print(aux.getId() + " ");
            } else {
                visitLevel(aux.getHijoIzq(),level,cont+1);
                visitLevel(aux.getHijoDer(),level,cont+1);
            }
    	}
    }
    
    /**Given the root/subroot of a tree, prune its leaves
     * @param aux root/subroot
     */
    public void pruneTree(BinaryNode aux) {
    	if (aux != null) {
            if (aux.esPadreDeHoja()) {
    		if (aux.getHijoIzq() != null) 
                    if (aux.getHijoIzq().isLeaf()) aux.setHijoIzq(null);
             
    		if (aux.getHijoDer() != null)
                    if (aux.getHijoDer().isLeaf()) aux.setHijoDer(null);  
            }
            if (root.isLeaf()) root = null;
            pruneTree(aux.getHijoIzq());
            pruneTree(aux.getHijoDer());
    	}
    }
    
    // dado un root/subroot DE ÁRBOL 1 y, un root/subroot DE ÁRBOL 2, devuelve un tercer árbol con los nodos en común
    // los árboles a comparar comienzan en el nodo enviado
    public static BinaryTree getIntersectionTree(BinaryNode aux, BinaryNode aux2) {
        if (aux == null || aux2 == null) return null;
        if (hasNodeOrderedTree(aux.getId(),aux2)) intersectionTree.insertNode(aux, intersectionTree.getRoot());
        getIntersectionTree(aux.getHijoIzq(),aux2);
        getIntersectionTree(aux.getHijoDer(),aux2);
        return intersectionTree;
    }private static BinaryTree intersectionTree;
    
    // método de prueba para obtener un nuevo árbol con los ids del vector enviado
    public static BinaryTree llenarYretornarArbolConEnteros(int[] vec) {
        BinaryTree bt = new BinaryTree(null);
        for(int i=0;i<vec.length;i++){
            BinaryNode bn = new BinaryNode(vec[i]);
            bt.insertNode(bn, bt.getRoot());
        }
        return bt;
    }
    
    // método que, apoyado en el de abajo, imprime un árbol en forma de diagrama
    public void imprimeArbolEjercicio() {
        System.out.println("--  Árbol impreso  --  "); 
        this.linea = "";
        for(int i=0;i<getHeight(getRoot(), 1);i++) {
            imprimeNivelEjercicio(getRoot(), i, 0, 0, 120);
            System.out.println(linea);
            linea = "";
        }
    }
    
    // método que ayuda a imprimir un nivel de un árbol binario considerando separar los nodos cierta cantidad de espacio entre sí
    // el método es muy parecido a visitLevel, pero se agregó min y max, que son los rangos de espacio donde se debe imprimir un nodo
    private void imprimeNivelEjercicio(BinaryNode aux, int level, int cont, int min, int max) {
        if (aux != null) {
            int ubicacion = (min + max) / 2;
            if (cont == level) {
                int espacios = ubicacion - linea.length();
                for(int i=0;i<espacios;i++) { linea += " "; }
                linea += aux.getId();
            } else {
                imprimeNivelEjercicio(aux.getHijoIzq(),level,cont+1,min,ubicacion);
                imprimeNivelEjercicio(aux.getHijoDer(),level,cont+1,ubicacion,max);
            }
    	}
    }private String linea;
}