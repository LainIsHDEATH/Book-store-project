package com.epam.rd.autocode.spring.project.repo;

import com.epam.rd.autocode.spring.project.model.Book;
import com.epam.rd.autocode.spring.project.model.enums.Language;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    Optional<Book> findByName(String name);
    boolean existsByName(String name);
    void deleteByName(String name);

    @Query("""
            select b from Book b
            where (:q is null or lower(b.name) like lower(concat('%',:q,'%')) or lower(b.author) like lower(concat('%',:q,'%')))
            and (:genre is null or b.genre = :genre)
            and (:language is null or b.language = :language)
            """)
    Page<Book> search(@Param("q") String q, @Param("genre") String genre, @Param("language") Language language, Pageable pageable);
}
