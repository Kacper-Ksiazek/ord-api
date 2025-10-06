package com.ord.shared.models

import java.util.*

/**
 * Base interface for entities that are associated with a user and have an identifier.
 *
 * This interface defines the common structure for entities that:
 * - Have their own unique identifier (which can be null for new entities)
 * - Are owned by or associated with a specific user
 * - May optionally hold a reference to the user entity
 */
interface IdentifiableUserResource {
    /**
     * The unique identifier of this resource.
     *
     * This field should be null for new entities that haven't been persisted to the database yet.
     * When null, the database will recognize the entity as new and perform an INSERT operation.
     * When non-null, the database will treat it as an existing entity and perform an UPDATE operation.
     */
    val id: UUID?

    /**
     * The unique identifier of the user who owns or is associated with this resource.
     *
     * This field establishes the relationship between the resource and its owning user.
     * It should always be non-null and correspond to an existing user's ID in the system.
     */
    val userId: UUID
}