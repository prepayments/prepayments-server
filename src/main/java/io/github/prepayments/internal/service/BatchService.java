package io.github.prepayments.internal.service;

import java.util.List;

/**
 * This interface is intended to be implemented transactionally
 *  but with batch items persisted for every commit. We are also assuming
 * that the client uses a search engine. I mean the fact that you are using this,
 * right?
 */
public interface BatchService<T> {

    /**
     * Save an entity.
     *
     * @param entities entity to save.
     * @return the persisted entity.
     */
    List<T> save(List<T> entities);

    /**
     * The above call only persists entities to the relations db repository
     * for efficiency sake.
     * Therefore to have it all in an index one needs to call this function
     * @param entities
     * @return
     */
    void index(List<T> entities);
}
