package vn.hcmute.springboot.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.hcmute.springboot.model.Skill;

public interface SkillRepository extends JpaRepository<Skill, Integer> {
  @Query("SELECT s FROM Skill s WHERE s.title = :title")
  Skill findByName(@Param("title") String title);
  List<Skill> findByTitleIn(List<String> skillNames);


}
