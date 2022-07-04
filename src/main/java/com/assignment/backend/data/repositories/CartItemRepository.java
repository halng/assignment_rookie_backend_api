package com.assignment.backend.data.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.assignment.backend.data.entities.CartItem;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Integer> {

}
