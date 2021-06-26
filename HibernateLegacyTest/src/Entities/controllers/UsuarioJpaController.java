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
import Entities.Codigoseg;
import Entities.Perfil;
import Entities.Reserva;
import Entities.Usuario;
import Entities.controllers.exceptions.IllegalOrphanException;
import Entities.controllers.exceptions.NonexistentEntityException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Leo
 */
public class UsuarioJpaController implements Serializable {

    public UsuarioJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Usuario usuario) {
        if (usuario.getReservaList() == null) {
            usuario.setReservaList(new ArrayList<Reserva>());
        }
        if (usuario.getReservaList1() == null) {
            usuario.setReservaList1(new ArrayList<Reserva>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Codigoseg codigoseg = usuario.getCodigoseg();
            if (codigoseg != null) {
                codigoseg = em.getReference(codigoseg.getClass(), codigoseg.getUsuario());
                usuario.setCodigoseg(codigoseg);
            }
            Perfil perfil = usuario.getPerfil();
            if (perfil != null) {
                perfil = em.getReference(perfil.getClass(), perfil.getIdPerfil());
                usuario.setPerfil(perfil);
            }
            List<Reserva> attachedReservaList = new ArrayList<Reserva>();
            for (Reserva reservaListReservaToAttach : usuario.getReservaList()) {
                reservaListReservaToAttach = em.getReference(reservaListReservaToAttach.getClass(), reservaListReservaToAttach.getIdReserva());
                attachedReservaList.add(reservaListReservaToAttach);
            }
            usuario.setReservaList(attachedReservaList);
            List<Reserva> attachedReservaList1 = new ArrayList<Reserva>();
            for (Reserva reservaList1ReservaToAttach : usuario.getReservaList1()) {
                reservaList1ReservaToAttach = em.getReference(reservaList1ReservaToAttach.getClass(), reservaList1ReservaToAttach.getIdReserva());
                attachedReservaList1.add(reservaList1ReservaToAttach);
            }
            usuario.setReservaList1(attachedReservaList1);
            em.persist(usuario);
            if (codigoseg != null) {
                Usuario oldUsuario1OfCodigoseg = codigoseg.getUsuario1();
                if (oldUsuario1OfCodigoseg != null) {
                    oldUsuario1OfCodigoseg.setCodigoseg(null);
                    oldUsuario1OfCodigoseg = em.merge(oldUsuario1OfCodigoseg);
                }
                codigoseg.setUsuario1(usuario);
                codigoseg = em.merge(codigoseg);
            }
            if (perfil != null) {
                perfil.getUsuarioList().add(usuario);
                perfil = em.merge(perfil);
            }
            for (Reserva reservaListReserva : usuario.getReservaList()) {
                reservaListReserva.getUsuarioList().add(usuario);
                reservaListReserva = em.merge(reservaListReserva);
            }
            for (Reserva reservaList1Reserva : usuario.getReservaList1()) {
                Usuario oldResponsableOfReservaList1Reserva = reservaList1Reserva.getResponsable();
                reservaList1Reserva.setResponsable(usuario);
                reservaList1Reserva = em.merge(reservaList1Reserva);
                if (oldResponsableOfReservaList1Reserva != null) {
                    oldResponsableOfReservaList1Reserva.getReservaList1().remove(reservaList1Reserva);
                    oldResponsableOfReservaList1Reserva = em.merge(oldResponsableOfReservaList1Reserva);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Usuario usuario) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Usuario persistentUsuario = em.find(Usuario.class, usuario.getIdUsuario());
            Codigoseg codigosegOld = persistentUsuario.getCodigoseg();
            Codigoseg codigosegNew = usuario.getCodigoseg();
            Perfil perfilOld = persistentUsuario.getPerfil();
            Perfil perfilNew = usuario.getPerfil();
            List<Reserva> reservaListOld = persistentUsuario.getReservaList();
            List<Reserva> reservaListNew = usuario.getReservaList();
            List<Reserva> reservaList1Old = persistentUsuario.getReservaList1();
            List<Reserva> reservaList1New = usuario.getReservaList1();
            List<String> illegalOrphanMessages = null;
            if (codigosegOld != null && !codigosegOld.equals(codigosegNew)) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("You must retain Codigoseg " + codigosegOld + " since its usuario1 field is not nullable.");
            }
            for (Reserva reservaList1OldReserva : reservaList1Old) {
                if (!reservaList1New.contains(reservaList1OldReserva)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Reserva " + reservaList1OldReserva + " since its responsable field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (codigosegNew != null) {
                codigosegNew = em.getReference(codigosegNew.getClass(), codigosegNew.getUsuario());
                usuario.setCodigoseg(codigosegNew);
            }
            if (perfilNew != null) {
                perfilNew = em.getReference(perfilNew.getClass(), perfilNew.getIdPerfil());
                usuario.setPerfil(perfilNew);
            }
            List<Reserva> attachedReservaListNew = new ArrayList<Reserva>();
            for (Reserva reservaListNewReservaToAttach : reservaListNew) {
                reservaListNewReservaToAttach = em.getReference(reservaListNewReservaToAttach.getClass(), reservaListNewReservaToAttach.getIdReserva());
                attachedReservaListNew.add(reservaListNewReservaToAttach);
            }
            reservaListNew = attachedReservaListNew;
            usuario.setReservaList(reservaListNew);
            List<Reserva> attachedReservaList1New = new ArrayList<Reserva>();
            for (Reserva reservaList1NewReservaToAttach : reservaList1New) {
                reservaList1NewReservaToAttach = em.getReference(reservaList1NewReservaToAttach.getClass(), reservaList1NewReservaToAttach.getIdReserva());
                attachedReservaList1New.add(reservaList1NewReservaToAttach);
            }
            reservaList1New = attachedReservaList1New;
            usuario.setReservaList1(reservaList1New);
            usuario = em.merge(usuario);
            if (codigosegNew != null && !codigosegNew.equals(codigosegOld)) {
                Usuario oldUsuario1OfCodigoseg = codigosegNew.getUsuario1();
                if (oldUsuario1OfCodigoseg != null) {
                    oldUsuario1OfCodigoseg.setCodigoseg(null);
                    oldUsuario1OfCodigoseg = em.merge(oldUsuario1OfCodigoseg);
                }
                codigosegNew.setUsuario1(usuario);
                codigosegNew = em.merge(codigosegNew);
            }
            if (perfilOld != null && !perfilOld.equals(perfilNew)) {
                perfilOld.getUsuarioList().remove(usuario);
                perfilOld = em.merge(perfilOld);
            }
            if (perfilNew != null && !perfilNew.equals(perfilOld)) {
                perfilNew.getUsuarioList().add(usuario);
                perfilNew = em.merge(perfilNew);
            }
            for (Reserva reservaListOldReserva : reservaListOld) {
                if (!reservaListNew.contains(reservaListOldReserva)) {
                    reservaListOldReserva.getUsuarioList().remove(usuario);
                    reservaListOldReserva = em.merge(reservaListOldReserva);
                }
            }
            for (Reserva reservaListNewReserva : reservaListNew) {
                if (!reservaListOld.contains(reservaListNewReserva)) {
                    reservaListNewReserva.getUsuarioList().add(usuario);
                    reservaListNewReserva = em.merge(reservaListNewReserva);
                }
            }
            for (Reserva reservaList1NewReserva : reservaList1New) {
                if (!reservaList1Old.contains(reservaList1NewReserva)) {
                    Usuario oldResponsableOfReservaList1NewReserva = reservaList1NewReserva.getResponsable();
                    reservaList1NewReserva.setResponsable(usuario);
                    reservaList1NewReserva = em.merge(reservaList1NewReserva);
                    if (oldResponsableOfReservaList1NewReserva != null && !oldResponsableOfReservaList1NewReserva.equals(usuario)) {
                        oldResponsableOfReservaList1NewReserva.getReservaList1().remove(reservaList1NewReserva);
                        oldResponsableOfReservaList1NewReserva = em.merge(oldResponsableOfReservaList1NewReserva);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = usuario.getIdUsuario();
                if (findUsuario(id) == null) {
                    throw new NonexistentEntityException("The usuario with id " + id + " no longer exists.");
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
            Usuario usuario;
            try {
                usuario = em.getReference(Usuario.class, id);
                usuario.getIdUsuario();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The usuario with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Codigoseg codigosegOrphanCheck = usuario.getCodigoseg();
            if (codigosegOrphanCheck != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Usuario (" + usuario + ") cannot be destroyed since the Codigoseg " + codigosegOrphanCheck + " in its codigoseg field has a non-nullable usuario1 field.");
            }
            List<Reserva> reservaList1OrphanCheck = usuario.getReservaList1();
            for (Reserva reservaList1OrphanCheckReserva : reservaList1OrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Usuario (" + usuario + ") cannot be destroyed since the Reserva " + reservaList1OrphanCheckReserva + " in its reservaList1 field has a non-nullable responsable field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Perfil perfil = usuario.getPerfil();
            if (perfil != null) {
                perfil.getUsuarioList().remove(usuario);
                perfil = em.merge(perfil);
            }
            List<Reserva> reservaList = usuario.getReservaList();
            for (Reserva reservaListReserva : reservaList) {
                reservaListReserva.getUsuarioList().remove(usuario);
                reservaListReserva = em.merge(reservaListReserva);
            }
            em.remove(usuario);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Usuario> findUsuarioEntities() {
        return findUsuarioEntities(true, -1, -1);
    }

    public List<Usuario> findUsuarioEntities(int maxResults, int firstResult) {
        return findUsuarioEntities(false, maxResults, firstResult);
    }

    private List<Usuario> findUsuarioEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Usuario.class));
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

    public Usuario findUsuario(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Usuario.class, id);
        } finally {
            em.close();
        }
    }

    public int getUsuarioCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Usuario> rt = cq.from(Usuario.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
