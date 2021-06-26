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
import Entities.Cubiculo;
import Entities.Horario;
import Entities.HorarioPK;
import Entities.controllers.exceptions.NonexistentEntityException;
import Entities.controllers.exceptions.PreexistingEntityException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Leo
 */
public class HorarioJpaController implements Serializable {

    public HorarioJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Horario horario) throws PreexistingEntityException, Exception {
        if (horario.getHorarioPK() == null) {
            horario.setHorarioPK(new HorarioPK());
        }
        horario.getHorarioPK().setCubiculo(horario.getCubiculo1().getIdCubiculo());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Cubiculo cubiculo1 = horario.getCubiculo1();
            if (cubiculo1 != null) {
                cubiculo1 = em.getReference(cubiculo1.getClass(), cubiculo1.getIdCubiculo());
                horario.setCubiculo1(cubiculo1);
            }
            em.persist(horario);
            if (cubiculo1 != null) {
                cubiculo1.getHorarioList().add(horario);
                cubiculo1 = em.merge(cubiculo1);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findHorario(horario.getHorarioPK()) != null) {
                throw new PreexistingEntityException("Horario " + horario + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Horario horario) throws NonexistentEntityException, Exception {
        horario.getHorarioPK().setCubiculo(horario.getCubiculo1().getIdCubiculo());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Horario persistentHorario = em.find(Horario.class, horario.getHorarioPK());
            Cubiculo cubiculo1Old = persistentHorario.getCubiculo1();
            Cubiculo cubiculo1New = horario.getCubiculo1();
            if (cubiculo1New != null) {
                cubiculo1New = em.getReference(cubiculo1New.getClass(), cubiculo1New.getIdCubiculo());
                horario.setCubiculo1(cubiculo1New);
            }
            horario = em.merge(horario);
            if (cubiculo1Old != null && !cubiculo1Old.equals(cubiculo1New)) {
                cubiculo1Old.getHorarioList().remove(horario);
                cubiculo1Old = em.merge(cubiculo1Old);
            }
            if (cubiculo1New != null && !cubiculo1New.equals(cubiculo1Old)) {
                cubiculo1New.getHorarioList().add(horario);
                cubiculo1New = em.merge(cubiculo1New);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                HorarioPK id = horario.getHorarioPK();
                if (findHorario(id) == null) {
                    throw new NonexistentEntityException("The horario with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(HorarioPK id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Horario horario;
            try {
                horario = em.getReference(Horario.class, id);
                horario.getHorarioPK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The horario with id " + id + " no longer exists.", enfe);
            }
            Cubiculo cubiculo1 = horario.getCubiculo1();
            if (cubiculo1 != null) {
                cubiculo1.getHorarioList().remove(horario);
                cubiculo1 = em.merge(cubiculo1);
            }
            em.remove(horario);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Horario> findHorarioEntities() {
        return findHorarioEntities(true, -1, -1);
    }

    public List<Horario> findHorarioEntities(int maxResults, int firstResult) {
        return findHorarioEntities(false, maxResults, firstResult);
    }

    private List<Horario> findHorarioEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Horario.class));
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

    public Horario findHorario(HorarioPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Horario.class, id);
        } finally {
            em.close();
        }
    }

    public int getHorarioCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Horario> rt = cq.from(Horario.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
