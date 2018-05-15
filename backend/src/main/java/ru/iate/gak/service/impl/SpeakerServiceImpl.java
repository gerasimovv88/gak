package ru.iate.gak.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import padeg.lib.Padeg;
import ru.iate.gak.domain.Gender;
import ru.iate.gak.domain.Role;
import ru.iate.gak.domain.Speaker;
import ru.iate.gak.domain.Status;
import ru.iate.gak.model.*;
import ru.iate.gak.repository.CommissionRepository;
import ru.iate.gak.repository.GroupRepository;
import ru.iate.gak.repository.SpeakerRepository;
import ru.iate.gak.repository.StudentRepository;
import ru.iate.gak.service.SpeakerService;
import ru.iate.gak.service.TexService;

import javax.transaction.Transactional;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SpeakerServiceImpl implements SpeakerService {

    @Autowired
    private SpeakerRepository speakerRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private TexService texService;

    @Autowired
    private CommissionRepository commissionRepository;

    /**
     * Clean existing speakers and fill new:
     * 1. Delete speaker.
     * 2. If new speaker has date, then save new speaker, else no save.
     */
    @Override
    @Transactional
    public void fillList(List<Speaker> speakers) {
        List<SpeakerEntity> result = new ArrayList<>();
        speakers.forEach(s -> {
            if (s.getStudent() != null && s.getStudent().getId() != null) {
                StudentEntity studentEntity = studentRepository.findOne(s.getStudent().getId());
                if (studentEntity == null)
                    throw new RuntimeException("Студент с id = " + s.getStudent().getId() + " не найден");

                speakerRepository.deleteByStudent(studentEntity);

                if (s.getDate() != null) {
                    SpeakerEntity speakerEntity = new SpeakerEntity();
                    speakerEntity.setListId(s.getListId());
                    speakerEntity.setDate(s.getDate());
                    speakerEntity.setStudent(studentEntity);
                    speakerEntity.setOrderOfSpeaking(s.getOrderOfSpeaking());
                    result.add(speakerEntity);
                }
            } else throw new RuntimeException("Нет id у студента");
        });
        speakerRepository.save(result);
    }

    @Override
    @Transactional
    public List<Speaker> getSpeakerListOfCurrentGroupOfDay(String group, LocalDateTime date) {
        GroupEntity groupEntity = groupRepository.findOne(group);
        if (groupEntity == null) throw new RuntimeException("Группа с названием " + group + "  не найдена");

        if (date == null) {
            return speakerRepository.getSpeakersListOfCurrentGroup(groupEntity).stream().map(Speaker::new).collect(Collectors.toList());
        } else {
            return speakerRepository.getSpeakersListOfCurrentGroupOfDay(groupEntity, date).stream().map(Speaker::new).collect(Collectors.toList());
        }
    }

    @Override
    @Transactional
    public Map<String, List<Speaker>> getAllSpeakersListAllGroupsOfDay(LocalDateTime date) {
        Map<String, List<Speaker>> result = new HashMap<>();
        groupRepository.getAllOrderByTitleAsc().forEach(groupEntity -> {   //get all groups and get speakers of these groups
            result.put(groupEntity.getTitle(), speakerRepository.getSpeakersListOfCurrentGroupOfDay(groupEntity, date).stream().map(Speaker::new).collect(Collectors.toList()));
        });
        return result;
    }

    @Override
    @Transactional
    public List<File> getSpeakerProtocolsOfGroup(String group) {
        GroupEntity groupEntity = groupRepository.findOne(group);
        if (groupEntity == null) throw new RuntimeException("Группа с названием " + group + "  не найдена");

        List<File> result = new ArrayList<>();
        List<SpeakerEntity> speakers = speakerRepository.getSpeakersListOfCurrentGroup(groupEntity);
        speakers.forEach(s -> {
            if (s.getStudent() == null) return;
            if (s.getStudent().getDiplom() == null) return;
            if (s.getStudent().getDiplom().getMentor() == null || s.getStudent().getDiplom().getReviewer() == null)
                return;

            Map<String, String> params = new HashMap<>();
            params.put("@date", s.getDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
            params.put("@number", s.getOrderOfSpeaking().toString());

            boolean gender = false;     //false for FEMALE
            if (s.getStudent().getGender() != null && s.getStudent().getGender().equals(Gender.MALE)) gender = true;

            String studentName = (s.getStudent().getMiddlename() == null) || (s.getStudent().getMiddlename().isEmpty()) ?
                    Padeg.getFIOPadeg(s.getStudent().getLastname(), s.getStudent().getFirstname(), "", gender, 2):
                    Padeg.getFIOPadeg(s.getStudent().getLastname(), s.getStudent().getFirstname(), s.getStudent().getMiddlename(), gender, 2);
            params.put("@studentRodit", studentName);
            params.put("@studentLastname", s.getStudent().getLastname());

            String studentIO = (s.getStudent().getMiddlename() == null) || (s.getStudent().getMiddlename().isEmpty()) ?
                    s.getStudent().getLastname() + " " + s.getStudent().getFirstname().charAt(0) + "." :
                    s.getStudent().getLastname() + " " + s.getStudent().getFirstname().charAt(0) + ". " + s.getStudent().getMiddlename().charAt(0) + ".";
            params.put("@studentIOIm", studentIO);

            String studentIORodit = (s.getStudent().getMiddlename() == null) || (s.getStudent().getMiddlename().isEmpty()) ?
                    Padeg.getFIOPadeg(s.getStudent().getLastname(), s.getStudent().getFirstname().charAt(0) + ".", "", gender, 2):
                    Padeg.getFIOPadeg(s.getStudent().getLastname(), s.getStudent().getFirstname().charAt(0) + ".", s.getStudent().getMiddlename().charAt(0) + ".", gender, 2);
            params.put("@studentIORodit", studentIORodit);

            String studentIODatel = (s.getStudent().getMiddlename() == null) || (s.getStudent().getMiddlename().isEmpty()) ?
                    Padeg.getFIOPadeg(s.getStudent().getLastname(), s.getStudent().getFirstname().charAt(0) + ".", "", gender, 3):
                    Padeg.getFIOPadeg(s.getStudent().getLastname(), s.getStudent().getFirstname().charAt(0) + ".", s.getStudent().getMiddlename().charAt(0) + ".", gender, 3);
            params.put("@studentIODatel", studentIODatel);

            String studentIOVinit = (s.getStudent().getMiddlename() == null) || (s.getStudent().getMiddlename().isEmpty()) ?
                    Padeg.getFIOPadeg(s.getStudent().getLastname(), s.getStudent().getFirstname().charAt(0) + ".", "", gender, 4):
                    Padeg.getFIOPadeg(s.getStudent().getLastname(), s.getStudent().getFirstname().charAt(0) + ".", s.getStudent().getMiddlename().charAt(0) + ".", gender, 4);
            params.put("@studentIOVinit", studentIOVinit);

            String studentIOTvor = (s.getStudent().getMiddlename() == null) || (s.getStudent().getMiddlename().isEmpty()) ?
                    Padeg.getFIOPadeg(s.getStudent().getLastname(), s.getStudent().getFirstname().charAt(0) + ".", "", gender, 5):
                    Padeg.getFIOPadeg(s.getStudent().getLastname(), s.getStudent().getFirstname().charAt(0) + ".", s.getStudent().getMiddlename().charAt(0) + ".", gender, 5);
            params.put("@studentIOTvor", studentIOTvor);

            if (gender) {
                params.put("@genderStudentImen", "студент");
                params.put("@genderStudentRodit", "студента");
                params.put("@genderStudentDatel", "студенту");
                params.put("@genderStudentTvorit", "студентом");
                params.put("@genderDefImen", "защитил");
                params.put("@genderOutRodit", "выпускника");
                params.put("@genderOutDatel", "выпускнику");
            } else {
                params.put("@genderStudentImen", "студентка");
                params.put("@genderStudentRodit", "студентки");
                params.put("@genderStudentDatel", "студентке");
                params.put("@genderStudentTvorit", "студенткой");
                params.put("@genderDefImen", "защитила");
                params.put("@genderOutRodit", "выпускницу");
                params.put("@genderOutDatel", "выпускнице");
            }

            params.put("@title", s.getStudent().getDiplom().getTitle());

            boolean mentorGender = false; //false for FEMALE
            if (s.getStudent().getDiplom().getMentor().getGender() != null && s.getStudent().getDiplom().getMentor().getGender().equals(Gender.MALE)) mentorGender = true;

            String mentorName = (s.getStudent().getDiplom().getMentor().getMiddlename() == null) || (s.getStudent().getDiplom().getMentor().getMiddlename().isEmpty()) ?
                    s.getStudent().getDiplom().getMentor().getLastname() + " " + s.getStudent().getDiplom().getMentor().getFirstname().charAt(0) + "." :
                    s.getStudent().getDiplom().getMentor().getLastname() + " " + s.getStudent().getDiplom().getMentor().getFirstname().charAt(0) + ". " + s.getStudent().getDiplom().getMentor().getMiddlename().charAt(0) + ".";
            params.put("@mentorIO", mentorName);

            String mentorRoditName = (s.getStudent().getDiplom().getMentor().getMiddlename() == null) || (s.getStudent().getDiplom().getMentor().getMiddlename().isEmpty()) ?
                    Padeg.getFIOPadeg(s.getStudent().getDiplom().getMentor().getLastname(), s.getStudent().getDiplom().getMentor().getFirstname(), "", mentorGender,2) :
                    Padeg.getFIOPadeg(s.getStudent().getDiplom().getMentor().getLastname(), s.getStudent().getDiplom().getMentor().getFirstname(), s.getStudent().getDiplom().getMentor().getMiddlename(), mentorGender, 2);
            params.put("@mentorRodit", mentorRoditName);

            String reviewerName = (s.getStudent().getDiplom().getReviewer().getMiddlename() == null) || (s.getStudent().getDiplom().getReviewer().getMiddlename().isEmpty()) ?
                    s.getStudent().getDiplom().getReviewer().getLastname() + " " + s.getStudent().getDiplom().getReviewer().getFirstname().charAt(0) + "." :
                    s.getStudent().getDiplom().getReviewer().getLastname() + " " + s.getStudent().getDiplom().getReviewer().getFirstname().charAt(0) + ". " + s.getStudent().getDiplom().getReviewer().getMiddlename().charAt(0) + ".";
            params.put("@reviewerIO", reviewerName);

            Set<QuestionEntity> questions = s.getStudent().getDiplom().getQuestions();
            final int[] i = {1};      //only 6 first question write in protocol
            questions.forEach(q -> {
                if (i[0] > 6) return;
                params.put("@question" + i[0], q.getQuestionText());
                i[0]++;
            });

            List<CommissionEntity> commissions = commissionRepository.getCommissionEntitiesByListId(1);
            final int[] k = {1}; //only 4 gak member + president + secretary must be
            commissions.forEach(c -> {
                if (c.getUser() == null) return;

                String userName = (c.getUser().getMiddlename() == null) || (c.getUser().getMiddlename().isEmpty()) ?
                        c.getUser().getLastname() + " " + c.getUser().getFirstname().charAt(0) + "." :
                        c.getUser().getLastname() + " " + c.getUser().getFirstname().charAt(0) + ". " + c.getUser().getMiddlename().charAt(0) + ".";
                if (c.getUser().getRoles().contains(Role.PRESIDENT)) {
                    params.put("@president", userName);
                    return;
                }
                if (c.getUser().getRoles().contains(Role.SECRETARY)) {
                    params.put("@secretary", userName);
                    return;
                }
                if (c.getUser().getRoles().contains(Role.MEMBER)) {
                    if (k[0] > 4) return;
                    params.put("@member" + k[0], userName);
                    k[0]++;
                    return;
                }
            });

            params.put("@mark5", null);
            params.put("@mark4", null);
            params.put("@mark3", null);
            params.put("@markAverage", null);
            params.put("@time", null);

            result.add(texService.exportDocuments(params));
        });

        return result;
    }

    @Override
    @Transactional
    public Speaker updateDiplomStatus(Long speakerId, Status status) {
        if (speakerId > 0) {
            SpeakerEntity speakerEntity = speakerRepository.getOne(speakerId);
            if (speakerEntity != null) {
                if (speakerEntity.getStudent() != null) {
                    if (speakerEntity.getStudent().getDiplom() != null) {
                        speakerEntity.getStudent().getDiplom().setStatus(status);
                        speakerRepository.save(speakerEntity);

                        if (speakerEntity == null) throw new RuntimeException("Произошла ошибка");

                        return new Speaker(speakerEntity);
                    } else throw new RuntimeException("Произошла ошибка");
                } else throw new RuntimeException("Произошла ошибка");
            } else throw new RuntimeException("Произошла ошибка");
        } else throw new RuntimeException("Произошла ошибка");
    }


}
