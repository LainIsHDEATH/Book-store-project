package com.epam.rd.autocode.spring.project.repo;

import com.epam.rd.autocode.spring.project.model.Book;
import com.epam.rd.autocode.spring.project.model.enums.AgeGroup;
import com.epam.rd.autocode.spring.project.model.enums.Genre;
import com.epam.rd.autocode.spring.project.model.enums.Language;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookRepository extends JpaRepository<Book, Long> {

    @Query("SELECT b FROM Book b WHERE " +
            "(:keyword IS NULL OR LOWER(b.nameEn) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(b.nameUk) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "(:genre IS NULL OR b.genre = :genre) AND " +
            "(:ageGroup IS NULL OR b.ageGroup = :ageGroup) AND " +
            "(:language IS NULL OR b.language = :language)")
    Page<Book> search(@Param("keyword") String keyword,
                      @Param("genre") Genre genre,
                      @Param("ageGroup") AgeGroup ageGroup,
                      @Param("language") Language language,
                      Pageable pageable);
}
