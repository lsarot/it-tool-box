/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entities.controllers;

import Entities.Prestamo;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import Entities.Reserva;
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
public class PrestamoJpaController implements Serializable {

    public PrestamoJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Prestamo prestamo) throws IllegalOrphanException, PreexistingEntityException, Exception {
        List<String> illegalOrphanMessages = null;
        Reserva reserva1OrphanCheck = prestamo.getReserva1();
        if (reserva1OrphanCheck != null) {
            Prestamo oldPrestamoOfReserva1 = reserva1OrphanCheck.getPrestamo();
            if (oldPrestamoOfReserva1 != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("The Reserva " + reserva1OrphanCheck + " already has an item of type Prestamo whose reserva1 column cannot be null. Please make another selection for the reserva1 field.");
            }
        }
        if (illegalOrphanMessages != null) {
            throw new IllegalOrphanException(illegalOrphanMessages);
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Reserva reserva1 = prestamo.getReserva1();
            if (reserva1 != null) {
                reserva1 = em.getReference(reserva1.getClass(), reserva1.getIdReserva());
                prestamo.setReserva1(reserva1);
            }
            em.persist(prestamo);
            if (reserva1 != null) {
                reserva1.setPrestamo(prestamo);
                reserva1 = em.merge(reserva1);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findPrestamo(prestamo.getReserva()) != null) {
                throw new PreexistingEntityException("Prestamo " + prestamo + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Prestamo prestamo) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Prestamo persistentPrestamo = em.find(Prestamo.class, prestamo.getReserva());
            Reserva reserva1Old = persistentPrestamo.getReserva1();
            Reserva reserva1New = prestamo.getReserva1();
            List<String> illegalOrphanMessages = null;
            if (reserva1New != null && !reserva1New.equals(reserva1Old)) {
                Prestamo oldPrestamoOfReserva1 = reserva1New.getPrestamo();
                if (oldPrestamoOfReserva1 != null) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("The Reserva " + reserva1New + " already has an item of type Prestamo whose reserva1 column cannot be null. Please make another selection for the reserva1 field.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (reserva1New != null) {
                reserva1New = em.getReference(reserva1New.getClass(), reserva1New.getIdReserva());
                prestamo.setReserva1(reserva1New);
            }
            prestamo = em.merge(prestamo);
            if (reserva1Old != null && !reserva1Old.equals(reserva1New)) {
                reserva1Old.setPrestamo(null);
                reserva1Old = em.merge(reserva1Old);
            }
            if (reserva1New != null && !reserva1New.equals(reserva1Old)) {
                reserva1New.setPrestamo(prestamo);
                reserva1New = em.merge(reserva1New);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = prestamo.getReserva();
                if (findPrestamo(id) == null) {
                    throw new NonexistentEntityException("The prestamo with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Prestamo prestamo;
            try {
                prestamo = em.getReference(Prestamo.class, id);
                prestamo.getReserva();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The prestamo with id " + id + " no longer exists.", enfe);
            }
            Reserva reserva1 = prestamo.getReserva1();
            if (reserva1 != null) {
                reserva1.setPrestamo(null);
                reserva1 = em.merge(reserva1);
            }
            em.remove(prestamo);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Prestamo> findPrestamoEntities() {
        return findPrestamoEntities(true, -1, -1);
    }

    public List<Prestamo> findPrestamoEntities(int maxResults, int firstResult) {
        return findPrestamoEntities(false, maxResults, firstResult);
    }

    private List<Prestamo> findPrestamoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Prestamo.class));
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

    public Prestamo findPrestamo(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Prestamo.class, id);
        } finally {
            em.close();
        }
    }

    public int getPrestamoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Prestamo> rt = cq.from(Prestamo.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
