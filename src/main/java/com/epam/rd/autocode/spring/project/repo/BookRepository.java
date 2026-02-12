package com.epam.rd.autocode.spring.project.repo;

import com.epam.rd.autocode.spring.project.model.Book;
import com.epam.rd.autocode.spring.project.model.enums.AgeGroup;
import com.epam.rd.autocode.spring.project.model.enums.Language;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findByNameEn(String nameEn);

    Optional<Book> findById(Long id);

    @Query("SELECT b FROM Book b WHERE " +
            "(:keyword IS NULL OR b.nameEn ILIKE %:keyword% OR b.nameUk ILIKE %:keyword%) AND " +
            "(:ageGroup IS NULL OR b.ageGroup = :ageGroup) AND " +
            "(:language IS NULL OR b.language = :language) AND " +
            "(:author IS NULL OR b.authorEn = :author OR b.authorUk = :author) AND " +
            "(:genre IS NULL OR b.genreEn = :genre OR b.genreUk = :genre)")
    Page<Book> findWithFilters(@Param("keyword") String keyword,
                               @Param("ageGroup") AgeGroup ageGroup,
                               @Param("language") Language language,
                               @Param("author") String author,
                               @Param("genre") String genre,
                               Pageable pageable);

    @Query("SELECT DISTINCT b.authorEn FROM Book b")
    List<String> findAllAuthors();

    @Query("SELECT DISTINCT b.genreEn, b.genreUk FROM Book b")
    List<Object[]> findAllGenresWithTranslations();

    @Query("SELECT DISTINCT b.language FROM Book b")
    List<String> findAllLanguages();

    @Query("select distinct b.genreEn from Book b")
    List<String> findAllEnGenres();
}
