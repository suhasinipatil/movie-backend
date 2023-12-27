package com.example.moviebackend.movie;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<MovieEntity, Integer> {

    //@Query("select m from movies m where m.id in (select ml.movie_id from movie_likes ml where ml.user_id = :userid)")
    //Optional<List<MovieEntity>> findAllByUsername(@Param("userid") Integer userid);

}
