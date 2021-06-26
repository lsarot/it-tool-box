/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entities.controllers;

import Entities.Cubiculo;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import Entities.Horario;
import java.util.ArrayList;
import java.util.List;
import Entities.Reserva;
import Entities.controllers.exceptions.IllegalOrphanException;
import Entities.controllers.exceptions.NonexistentEntityException;
import Entities.controllers.exceptions.PreexistingEntityException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Leo
 */
public class CubiculoJpaController implements Serializable {

    public CubiculoJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Cubiculo cubiculo) throws PreexistingEntityException, Exception {
        if (cubiculo.getHorarioList() == null) {
            cubiculo.setHorarioList(new ArrayList<Horario>());
        }
        if (cubiculo.getReservaList() == null) {
            cubiculo.setReservaList(new ArrayList<Reserva>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Horario> attachedHorarioList = new ArrayList<Horario>();
            for (Horario horarioListHorarioToAttach : cubiculo.getHorarioList()) {
                horarioListHorarioToAttach = em.getReference(horarioListHorarioToAttach.getClass(), horarioListHorarioToAttach.getHorarioPK());
                attachedHorarioList.add(horarioListHorarioToAttach);
            }
            cubiculo.setHorarioList(attachedHorarioList);
            List<Reserva> attachedReservaList = new ArrayList<Reserva>();
            for (Reserva reservaListReservaToAttach : cubiculo.getReservaList()) {
                reservaListReservaToAttach = em.getReference(reservaListReservaToAttach.getClass(), reservaListReservaToAttach.getIdReserva());
                attachedReservaList.add(reservaListReservaToAttach);
            }
            cubiculo.setReservaList(attachedReservaList);
            em.persist(cubiculo);
            for (Horario horarioListHorario : cubiculo.getHorarioList()) {
                Cubiculo oldCubiculo1OfHorarioListHorario = horarioListHorario.getCubiculo1();
                horarioListHorario.setCubiculo1(cubiculo);
                horarioListHorario = em.merge(horarioListHorario);
                if (oldCubiculo1OfHorarioListHorario != null) {
                    oldCubiculo1OfHorarioListHorario.getHorarioList().remove(horarioListHorario);
                    oldCubiculo1OfHorarioListHorario = em.merge(oldCubiculo1OfHorarioListHorario);
                }
            }
            for (Reserva reservaListReserva : cubiculo.getReservaList()) {
                Cubiculo oldCubiculoOfReservaListReserva = reservaListReserva.getCubiculo();
                reservaListReserva.setCubiculo(cubiculo);
                reservaListReserva = em.merge(reservaListReserva);
                if (oldCubiculoOfReservaListReserva != null) {
                    oldCubiculoOfReservaListReserva.getReservaList().remove(reservaListReserva);
                    oldCubiculoOfReservaListReserva = em.merge(oldCubiculoOfReservaListReserva);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findCubiculo(cubiculo.getIdCubiculo()) != null) {
                throw new PreexistingEntityException("Cubiculo " + cubiculo + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Cubiculo cubiculo) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Cubiculo persistentCubiculo = em.find(Cubiculo.class, cubiculo.getIdCubiculo());
            List<Horario> horarioListOld = persistentCubiculo.getHorarioList();
            List<Horario> horarioListNew = cubiculo.getHorarioList();
            List<Reserva> reservaListOld = persistentCubiculo.getReservaList();
            List<Reserva> reservaListNew = cubiculo.getReservaList();
            List<String> illegalOrphanMessages = null;
            for (Horario horarioListOldHorario : horarioListOld) {
                if (!horarioListNew.contains(horarioListOldHorario)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Horario " + horarioListOldHorario + " since its cubiculo1 field is not nullable.");
                }
            }
            for (Reserva reservaListOldReserva : reservaListOld) {
                if (!reservaListNew.contains(reservaListOldReserva)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Reserva " + reservaListOldReserva + " since its cubiculo field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Horario> attachedHorarioListNew = new ArrayList<Horario>();
            for (Horario horarioListNewHorarioToAttach : horarioListNew) {
                horarioListNewHorarioToAttach = em.getReference(horarioListNewHorarioToAttach.getClass(), horarioListNewHorarioToAttach.getHorarioPK());
                attachedHorarioListNew.add(horarioListNewHorarioToAttach);
            }
            horarioListNew = attachedHorarioListNew;
            cubiculo.setHorarioList(horarioListNew);
            List<Reserva> attachedReservaListNew = new ArrayList<Reserva>();
            for (Reserva reservaListNewReservaToAttach : reservaListNew) {
                reservaListNewReservaToAttach = em.getReference(reservaListNewReservaToAttach.getClass(), reservaListNewReservaToAttach.getIdReserva());
                attachedReservaListNew.add(reservaListNewReservaToAttach);
            }
            reservaListNew = attachedReservaListNew;
            cubiculo.setReservaList(reservaListNew);
            cubiculo = em.merge(cubiculo);
            for (Horario horarioListNewHorario : horarioListNew) {
                if (!horarioListOld.contains(horarioListNewHorario)) {
                    Cubiculo oldCubiculo1OfHorarioListNewHorario = horarioListNewHorario.getCubiculo1();
                    horarioListNewHorario.setCubiculo1(cubiculo);
                    horarioListNewHorario = em.merge(horarioListNewHorario);
                    if (oldCubiculo1OfHorarioListNewHorario != null && !oldCubiculo1OfHorarioListNewHorario.equals(cubiculo)) {
                        oldCubiculo1OfHorarioListNewHorario.getHorarioList().remove(horarioListNewHorario);
                        oldCubiculo1OfHorarioListNewHorario = em.merge(oldCubiculo1OfHorarioListNewHorario);
                    }
                }
            }
            for (Reserva reservaListNewReserva : reservaListNew) {
                if (!reservaListOld.contains(reservaListNewReserva)) {
                    Cubiculo oldCubiculoOfReservaListNewReserva = reservaListNewReserva.getCubiculo();
                    reservaListNewReserva.setCubiculo(cubiculo);
                    reservaListNewReserva = em.merge(reservaListNewReserva);
                    if (oldCubiculoOfReservaListNewReserva != null && !oldCubiculoOfReservaListNewReserva.equals(cubiculo)) {
                        oldCubiculoOfReservaListNewReserva.getReservaList().remove(reservaListNewReserva);
                        oldCubiculoOfReservaListNewReserva = em.merge(oldCubiculoOfReservaListNewReserva);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = cubiculo.getIdCubiculo();
                if (findCubiculo(id) == null) {
                    throw new NonexistentEntityException("The cubiculo with id " + id + " no longer exists.");
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
            Cubiculo cubiculo;
            try {
                cubiculo = em.getReference(Cubiculo.class, id);
                cubiculo.getIdCubiculo();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The cubiculo with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Horario> horarioListOrphanCheck = cubiculo.getHorarioList();
            for (Horario horarioListOrphanCheckHorario : horarioListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Cubiculo (" + cubiculo + ") cannot be destroyed since the Horario " + horarioListOrphanCheckHorario + " in its horarioList field has a non-nullable cubiculo1 field.");
            }
            List<Reserva> reservaListOrphanCheck = cubiculo.getReservaList();
            for (Reserva reservaListOrphanCheckReserva : reservaListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Cubiculo (" + cubiculo + ") cannot be destroyed since the Reserva " + reservaListOrphanCheckReserva + " in its reservaList field has a non-nullable cubiculo field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(cubiculo);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Cubiculo> findCubiculoEntities() {
        return findCubiculoEntities(true, -1, -1);
    }

    public List<Cubiculo> findCubiculoEntities(int maxResults, int firstResult) {
        return findCubiculoEntities(false, maxResults, firstResult);
    }

    private List<Cubiculo> findCubiculoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Cubiculo.class));
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

    public Cubiculo findCubiculo(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Cubiculo.class, id);
        } finally {
            em.close();
        }
    }

    public int getCubiculoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Cubiculo> rt = cq.from(Cubiculo.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
