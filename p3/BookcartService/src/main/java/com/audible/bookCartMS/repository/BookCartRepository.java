package com.audible.bookCartMS.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import com.audible.bookCartMS.model.BookCart;

@Repository
public interface BookCartRepository extends JpaRepository<BookCart, Integer> {

	Optional<BookCart> findByUserId(Integer userId);

}
