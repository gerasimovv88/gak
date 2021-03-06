package ru.iate.gak.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.iate.gak.model.GroupEntity;
import ru.iate.gak.model.SpeakerEntity;
import ru.iate.gak.model.StudentEntity;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SpeakerRepository extends JpaRepository<SpeakerEntity, Long> {

    @Query(value = "SELECT s FROM SpeakerEntity s WHERE (s.student.deletedTime IS NULL) AND (s.student.group = :group) ORDER BY s.date ASC, s.orderOfSpeaking ASC")
    List<SpeakerEntity> getSpeakersListOfCurrentGroup(@Param("group") GroupEntity group);

    void deleteByStudent(StudentEntity student);

    @Query(value = "SELECT s FROM SpeakerEntity s WHERE (s.student.deletedTime IS NULL) AND (s.student.group = :group) AND (s.date = :date) ORDER BY s.orderOfSpeaking ASC")
    List<SpeakerEntity> getSpeakersListOfCurrentGroupOfDay(@Param("group") GroupEntity group, @Param("date") LocalDateTime date);

    @Query(value = "SELECT s FROM SpeakerEntity s WHERE (s.student.deletedTime IS NULL) AND (s.student.group = :group) AND (s.date BETWEEN :prevDate AND :nextDate) ORDER BY s.orderOfSpeaking ASC")
    List<SpeakerEntity> getSpeakersListOfCurrentGroupBetweenDate(@Param("group") GroupEntity group, @Param("prevDate") LocalDateTime prevDate, @Param("nextDate") LocalDateTime nextDate);
}
