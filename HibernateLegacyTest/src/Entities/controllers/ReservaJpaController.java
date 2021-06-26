/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entities.controllers;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import Entities.Prestamo;
import Entities.Cubiculo;
import Entities.Reserva;
import Entities.Usuario;
import Entities.controllers.exceptions.IllegalOrphanException;
import Entities.controllers.exceptions.NonexistentEntityException;
import Entities.controllers.exceptions.PreexistingEntityException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Leo
 */
public class ReservaJpaController implements Serializable {

    public ReservaJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Reserva reserva) throws PreexistingEntityException, Exception {
        if (reserva.getUsuarioList() == null) {
            reserva.setUsuarioList(new ArrayList<Usuario>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Prestamo prestamo = reserva.getPrestamo();
            if (prestamo != null) {
                prestamo = em.getReference(prestamo.getClass(), prestamo.getReserva());
                reserva.setPrestamo(prestamo);
            }
            Cubiculo cubiculo = reserva.getCubiculo();
            if (cubiculo != null) {
                cubiculo = em.getReference(cubiculo.getClass(), cubiculo.getIdCubiculo());
                reserva.setCubiculo(cubiculo);
            }
            Usuario responsable = reserva.getResponsable();
            if (responsable != null) {
                responsable = em.getReference(responsable.getClass(), responsable.getIdUsuario());
                reserva.setResponsable(responsable);
            }
            List<Usuario> attachedUsuarioList = new ArrayList<Usuario>();
            for (Usuario usuarioListUsuarioToAttach : reserva.getUsuarioList()) {
                usuarioListUsuarioToAttach = em.getReference(usuarioListUsuarioToAttach.getClass(), usuarioListUsuarioToAttach.getIdUsuario());
                attachedUsuarioList.add(usuarioListUsuarioToAttach);
            }
            reserva.setUsuarioList(attachedUsuarioList);
            em.persist(reserva);
            if (prestamo != null) {
                Reserva oldReserva1OfPrestamo = prestamo.getReserva1();
                if (oldReserva1OfPrestamo != null) {
                    oldReserva1OfPrestamo.setPrestamo(null);
                    oldReserva1OfPrestamo = em.merge(oldReserva1OfPrestamo);
                }
                prestamo.setReserva1(reserva);
                prestamo = em.merge(prestamo);
            }
            if (cubiculo != null) {
                cubiculo.getReservaList().add(reserva);
                cubiculo = em.merge(cubiculo);
            }
            if (responsable != null) {
                responsable.getReservaList().add(reserva);
                responsable = em.merge(responsable);
            }
            for (Usuario usuarioListUsuario : reserva.getUsuarioList()) {
                usuarioListUsuario.getReservaList().add(reserva);
                usuarioListUsuario = em.merge(usuarioListUsuario);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findReserva(reserva.getIdReserva()) != null) {
                throw new PreexistingEntityException("Reserva " + reserva + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Reserva reserva) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Reserva persistentReserva = em.find(Reserva.class, reserva.getIdReserva());
            Prestamo prestamoOld = persistentReserva.getPrestamo();
            Prestamo prestamoNew = reserva.getPrestamo();
            Cubiculo cubiculoOld = persistentReserva.getCubiculo();
            Cubiculo cubiculoNew = reserva.getCubiculo();
            Usuario responsableOld = persistentReserva.getResponsable();
            Usuario responsableNew = reserva.getResponsable();
            List<Usuario> usuarioListOld = persistentReserva.getUsuarioList();
            List<Usuario> usuarioListNew = reserva.getUsuarioList();
            List<String> illegalOrphanMessages = null;
            if (prestamoOld != null && !prestamoOld.equals(prestamoNew)) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("You must retain Prestamo " + prestamoOld + " since its reserva1 field is not nullable.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (prestamoNew != null) {
                prestamoNew = em.getReference(prestamoNew.getClass(), prestamoNew.getReserva());
                reserva.setPrestamo(prestamoNew);
            }
            if (cubiculoNew != null) {
                cubiculoNew = em.getReference(cubiculoNew.getClass(), cubiculoNew.getIdCubiculo());
                reserva.setCubiculo(cubiculoNew);
            }
            if (responsableNew != null) {
                responsableNew = em.getReference(responsableNew.getClass(), responsableNew.getIdUsuario());
                reserva.setResponsable(responsableNew);
            }
            List<Usuario> attachedUsuarioListNew = new ArrayList<Usuario>();
            for (Usuario usuarioListNewUsuarioToAttach : usuarioListNew) {
                usuarioListNewUsuarioToAttach = em.getReference(usuarioListNewUsuarioToAttach.getClass(), usuarioListNewUsuarioToAttach.getIdUsuario());
                attachedUsuarioListNew.add(usuarioListNewUsuarioToAttach);
            }
            usuarioListNew = attachedUsuarioListNew;
            reserva.setUsuarioList(usuarioListNew);
            reserva = em.merge(reserva);
            if (prestamoNew != null && !prestamoNew.equals(prestamoOld)) {
                Reserva oldReserva1OfPrestamo = prestamoNew.getReserva1();
                if (oldReserva1OfPrestamo != null) {
                    oldReserva1OfPrestamo.setPrestamo(null);
                    oldReserva1OfPrestamo = em.merge(oldReserva1OfPrestamo);
                }
                prestamoNew.setReserva1(reserva);
                prestamoNew = em.merge(prestamoNew);
            }
            if (cubiculoOld != null && !cubiculoOld.equals(cubiculoNew)) {
                cubiculoOld.getReservaList().remove(reserva);
                cubiculoOld = em.merge(cubiculoOld);
            }
            if (cubiculoNew != null && !cubiculoNew.equals(cubiculoOld)) {
                cubiculoNew.getReservaList().add(reserva);
                cubiculoNew = em.merge(cubiculoNew);
            }
            if (responsableOld != null && !responsableOld.equals(responsableNew)) {
                responsableOld.getReservaList().remove(reserva);
                responsableOld = em.merge(responsableOld);
            }
            if (responsableNew != null && !responsableNew.equals(responsableOld)) {
                responsableNew.getReservaList().add(reserva);
                responsableNew = em.merge(responsableNew);
            }
            for (Usuario usuarioListOldUsuario : usuarioListOld) {
                if (!usuarioListNew.contains(usuarioListOldUsuario)) {
                    usuarioListOldUsuario.getReservaList().remove(reserva);
                    usuarioListOldUsuario = em.merge(usuarioListOldUsuario);
                }
            }
            for (Usuario usuarioListNewUsuario : usuarioListNew) {
                if (!usuarioListOld.contains(usuarioListNewUsuario)) {
                    usuarioListNewUsuario.getReservaList().add(reserva);
                    usuarioListNewUsuario = em.merge(usuarioListNewUsuario);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = reserva.getIdReserva();
                if (findReserva(id) == null) {
                    throw new NonexistentEntityException("The reserva with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Reserva reserva;
            try {
                reserva = em.getReference(Reserva.class, id);
                reserva.getIdReserva();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The reserva with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Prestamo prestamoOrphanCheck = reserva.getPrestamo();
            if (prestamoOrphanCheck != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Reserva (" + reserva + ") cannot be destroyed since the Prestamo " + prestamoOrphanCheck + " in its prestamo field has a non-nullable reserva1 field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Cubiculo cubiculo = reserva.getCubiculo();
            if (cubiculo != null) {
                cubiculo.getReservaList().remove(reserva);
                cubiculo = em.merge(cubiculo);
            }
            Usuario responsable = reserva.getResponsable();
            if (responsable != null) {
                responsable.getReservaList().remove(reserva);
                responsable = em.merge(responsable);
            }
            List<Usuario> usuarioList = reserva.getUsuarioList();
            for (Usuario usuarioListUsuario : usuarioList) {
                usuarioListUsuario.getReservaList().remove(reserva);
                usuarioListUsuario = em.merge(usuarioListUsuario);
            }
            em.remove(reserva);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Reserva> findReservaEntities() {
        return findReservaEntities(true, -1, -1);
    }

    public List<Reserva> findReservaEntities(int maxResults, int firstResult) {
        return findReservaEntities(false, maxResults, firstResult);
    }

    private List<Reserva> findReservaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Reserva.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Reserva findReserva(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Reserva.class, id);
        } finally {
            em.close();
        }
    }

    public int getReservaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Reserva> rt = cq.from(Reserva.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
