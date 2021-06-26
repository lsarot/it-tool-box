/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entities.controllers;

import Entities.Perfil;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import Entities.Permiso;
import java.util.ArrayList;
import java.util.List;
import Entities.Usuario;
import Entities.controllers.exceptions.IllegalOrphanException;
import Entities.controllers.exceptions.NonexistentEntityException;
import Entities.controllers.exceptions.PreexistingEntityException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Leo
 */
public class PerfilJpaController implements Serializable {

    public PerfilJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Perfil perfil) throws PreexistingEntityException, Exception {
        if (perfil.getPermisoList() == null) {
            perfil.setPermisoList(new ArrayList<Permiso>());
        }
        if (perfil.getUsuarioList() == null) {
            perfil.setUsuarioList(new ArrayList<Usuario>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Permiso> attachedPermisoList = new ArrayList<Permiso>();
            for (Permiso permisoListPermisoToAttach : perfil.getPermisoList()) {
                permisoListPermisoToAttach = em.getReference(permisoListPermisoToAttach.getClass(), permisoListPermisoToAttach.getIdPermiso());
                attachedPermisoList.add(permisoListPermisoToAttach);
            }
            perfil.setPermisoList(attachedPermisoList);
            List<Usuario> attachedUsuarioList = new ArrayList<Usuario>();
            for (Usuario usuarioListUsuarioToAttach : perfil.getUsuarioList()) {
                usuarioListUsuarioToAttach = em.getReference(usuarioListUsuarioToAttach.getClass(), usuarioListUsuarioToAttach.getIdUsuario());
                attachedUsuarioList.add(usuarioListUsuarioToAttach);
            }
            perfil.setUsuarioList(attachedUsuarioList);
            em.persist(perfil);
            for (Permiso permisoListPermiso : perfil.getPermisoList()) {
                Perfil oldPerfilOfPermisoListPermiso = permisoListPermiso.getPerfil();
                permisoListPermiso.setPerfil(perfil);
                permisoListPermiso = em.merge(permisoListPermiso);
                if (oldPerfilOfPermisoListPermiso != null) {
                    oldPerfilOfPermisoListPermiso.getPermisoList().remove(permisoListPermiso);
                    oldPerfilOfPermisoListPermiso = em.merge(oldPerfilOfPermisoListPermiso);
                }
            }
            for (Usuario usuarioListUsuario : perfil.getUsuarioList()) {
                Perfil oldPerfilOfUsuarioListUsuario = usuarioListUsuario.getPerfil();
                usuarioListUsuario.setPerfil(perfil);
                usuarioListUsuario = em.merge(usuarioListUsuario);
                if (oldPerfilOfUsuarioListUsuario != null) {
                    oldPerfilOfUsuarioListUsuario.getUsuarioList().remove(usuarioListUsuario);
                    oldPerfilOfUsuarioListUsuario = em.merge(oldPerfilOfUsuarioListUsuario);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findPerfil(perfil.getIdPerfil()) != null) {
                throw new PreexistingEntityException("Perfil " + perfil + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Perfil perfil) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Perfil persistentPerfil = em.find(Perfil.class, perfil.getIdPerfil());
            List<Permiso> permisoListOld = persistentPerfil.getPermisoList();
            List<Permiso> permisoListNew = perfil.getPermisoList();
            List<Usuario> usuarioListOld = persistentPerfil.getUsuarioList();
            List<Usuario> usuarioListNew = perfil.getUsuarioList();
            List<String> illegalOrphanMessages = null;
            for (Permiso permisoListOldPermiso : permisoListOld) {
                if (!permisoListNew.contains(permisoListOldPermiso)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Permiso " + permisoListOldPermiso + " since its perfil field is not nullable.");
                }
            }
            for (Usuario usuarioListOldUsuario : usuarioListOld) {
                if (!usuarioListNew.contains(usuarioListOldUsuario)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Usuario " + usuarioListOldUsuario + " since its perfil field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Permiso> attachedPermisoListNew = new ArrayList<Permiso>();
            for (Permiso permisoListNewPermisoToAttach : permisoListNew) {
                permisoListNewPermisoToAttach = em.getReference(permisoListNewPermisoToAttach.getClass(), permisoListNewPermisoToAttach.getIdPermiso());
                attachedPermisoListNew.add(permisoListNewPermisoToAttach);
            }
            permisoListNew = attachedPermisoListNew;
            perfil.setPermisoList(permisoListNew);
            List<Usuario> attachedUsuarioListNew = new ArrayList<Usuario>();
            for (Usuario usuarioListNewUsuarioToAttach : usuarioListNew) {
                usuarioListNewUsuarioToAttach = em.getReference(usuarioListNewUsuarioToAttach.getClass(), usuarioListNewUsuarioToAttach.getIdUsuario());
                attachedUsuarioListNew.add(usuarioListNewUsuarioToAttach);
            }
            usuarioListNew = attachedUsuarioListNew;
            perfil.setUsuarioList(usuarioListNew);
            perfil = em.merge(perfil);
            for (Permiso permisoListNewPermiso : permisoListNew) {
                if (!permisoListOld.contains(permisoListNewPermiso)) {
                    Perfil oldPerfilOfPermisoListNewPermiso = permisoListNewPermiso.getPerfil();
                    permisoListNewPermiso.setPerfil(perfil);
                    permisoListNewPermiso = em.merge(permisoListNewPermiso);
                    if (oldPerfilOfPermisoListNewPermiso != null && !oldPerfilOfPermisoListNewPermiso.equals(perfil)) {
                        oldPerfilOfPermisoListNewPermiso.getPermisoList().remove(permisoListNewPermiso);
                        oldPerfilOfPermisoListNewPermiso = em.merge(oldPerfilOfPermisoListNewPermiso);
                    }
                }
            }
            for (Usuario usuarioListNewUsuario : usuarioListNew) {
                if (!usuarioListOld.contains(usuarioListNewUsuario)) {
                    Perfil oldPerfilOfUsuarioListNewUsuario = usuarioListNewUsuario.getPerfil();
                    usuarioListNewUsuario.setPerfil(perfil);
                    usuarioListNewUsuario = em.merge(usuarioListNewUsuario);
                    if (oldPerfilOfUsuarioListNewUsuario != null && !oldPerfilOfUsuarioListNewUsuario.equals(perfil)) {
                        oldPerfilOfUsuarioListNewUsuario.getUsuarioList().remove(usuarioListNewUsuario);
                        oldPerfilOfUsuarioListNewUsuario = em.merge(oldPerfilOfUsuarioListNewUsuario);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = perfil.getIdPerfil();
                if (findPerfil(id) == null) {
                    throw new NonexistentEntityException("The perfil with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(String id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Perfil perfil;
            try {
                perfil = em.getReference(Perfil.class, id);
                perfil.getIdPerfil();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The perfil with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Permiso> permisoListOrphanCheck = perfil.getPermisoList();
            for (Permiso permisoListOrphanCheckPermiso : permisoListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Perfil (" + perfil + ") cannot be destroyed since the Permiso " + permisoListOrphanCheckPermiso + " in its permisoList field has a non-nullable perfil field.");
            }
            List<Usuario> usuarioListOrphanCheck = perfil.getUsuarioList();
            for (Usuario usuarioListOrphanCheckUsuario : usuarioListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Perfil (" + perfil + ") cannot be destroyed since the Usuario " + usuarioListOrphanCheckUsuario + " in its usuarioList field has a non-nullable perfil field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(perfil);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Perfil> findPerfilEntities() {
        return findPerfilEntities(true, -1, -1);
    }

    public List<Perfil> findPerfilEntities(int maxResults, int firstResult) {
        return findPerfilEntities(false, maxResults, firstResult);
    }

    private List<Perfil> findPerfilEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Perfil.class));
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

    public Perfil findPerfil(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Perfil.class, id);
        } finally {
            em.close();
        }
    }

    public int getPerfilCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Perfil> rt = cq.from(Perfil.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
