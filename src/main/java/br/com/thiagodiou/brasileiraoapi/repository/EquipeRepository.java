package br.com.thiagodiou.brasileiraoapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.thiagodiou.brasileiraoapi.entity.Equipe;

@Repository
public interface EquipeRepository extends JpaRepository<Equipe, Long>{

}
