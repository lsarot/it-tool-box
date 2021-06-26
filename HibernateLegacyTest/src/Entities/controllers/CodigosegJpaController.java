/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entities.controllers;

import Entities.Codigoseg;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
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
public class CodigosegJpaController implements Serializable {

    public CodigosegJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Codigoseg codigoseg) throws IllegalOrphanException, PreexistingEntityException, Exception {
        List<String> illegalOrphanMessages = null;
        Usuario usuario1OrphanCheck = codigoseg.getUsuario1();
        if (usuario1OrphanCheck != null) {
            Codigoseg oldCodigosegOfUsuario1 = usuario1OrphanCheck.getCodigoseg();
            if (oldCodigosegOfUsuario1 != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("The Usuario " + usuario1OrphanCheck + " already has an item of type Codigoseg whose usuario1 column cannot be null. Please make another selection for the usuario1 field.");
            }
        }
        if (illegalOrphanMessages != null) {
            throw new IllegalOrphanException(illegalOrphanMessages);
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Usuario usuario1 = codigoseg.getUsuario1();
            if (usuario1 != null) {
                usuario1 = em.getReference(usuario1.getClass(), usuario1.getIdUsuario());
                codigoseg.setUsuario1(usuario1);
            }
            em.persist(codigoseg);
            if (usuario1 != null) {
                usuario1.setCodigoseg(codigoseg);
                usuario1 = em.merge(usuario1);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findCodigoseg(codigoseg.getUsuario()) != null) {
                throw new PreexistingEntityException("Codigoseg " + codigoseg + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Codigoseg codigoseg) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Codigoseg persistentCodigoseg = em.find(Codigoseg.class, codigoseg.getUsuario());
            Usuario usuario1Old = persistentCodigoseg.getUsuario1();
            Usuario usuario1New = codigoseg.getUsuario1();
            List<String> illegalOrphanMessages = null;
            if (usuario1New != null && !usuario1New.equals(usuario1Old)) {
                Codigoseg oldCodigosegOfUsuario1 = usuario1New.getCodigoseg();
                if (oldCodigosegOfUsuario1 != null) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("The Usuario " + usuario1New + " already has an item of type Codigoseg whose usuario1 column cannot be null. Please make another selection for the usuario1 field.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (usuario1New != null) {
                usuario1New = em.getReference(usuario1New.getClass(), usuario1New.getIdUsuario());
                codigoseg.setUsuario1(usuario1New);
            }
            codigoseg = em.merge(codigoseg);
            if (usuario1Old != null && !usuario1Old.equals(usuario1New)) {
                usuario1Old.setCodigoseg(null);
                usuario1Old = em.merge(usuario1Old);
            }
            if (usuario1New != null && !usuario1New.equals(usuario1Old)) {
                usuario1New.setCodigoseg(codigoseg);
                usuario1New = em.merge(usuario1New);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = codigoseg.getUsuario();
                if (findCodigoseg(id) == null) {
                    throw new NonexistentEntityException("The codigoseg with id " + id + " no longer exists.");
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
            Codigoseg codigoseg;
            try {
                codigoseg = em.getReference(Codigoseg.class, id);
                codigoseg.getUsuario();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The codigoseg with id " + id + " no longer exists.", enfe);
            }
            Usuario usuario1 = codigoseg.getUsuario1();
            if (usuario1 != null) {
                usuario1.setCodigoseg(null);
                usuario1 = em.merge(usuario1);
            }
            em.remove(codigoseg);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Codigoseg> findCodigosegEntities() {
        return findCodigosegEntities(true, -1, -1);
    }

    public List<Codigoseg> findCodigosegEntities(int maxResults, int firstResult) {
        return findCodigosegEntities(false, maxResults, firstResult);
    }

    private List<Codigoseg> findCodigosegEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Codigoseg.class));
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

    public Codigoseg findCodigoseg(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Codigoseg.class, id);
        } finally {
            em.close();
        }
    }

    public int getCodigosegCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Codigoseg> rt = cq.from(Codigoseg.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
