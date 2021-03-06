package ru.iate.gak.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.iate.gak.dto.CriteriaDto;
import ru.iate.gak.dto.CriteriaDtoListWithResult;
import ru.iate.gak.dto.GeneralCriteriaDto;
import ru.iate.gak.dto.RatingDto;
import ru.iate.gak.security.GakSecured;
import ru.iate.gak.security.GakSecurityContext;
import ru.iate.gak.security.Roles;
import ru.iate.gak.service.CriteriaService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/criteria")
public class CriteriaController {

    @Autowired
    private CriteriaService criteriaService;

    @Autowired
    private GakSecurityContext securityContext;

    @GetMapping(value = "/defaults")
    @GakSecured(roles = {Roles.PRESIDENT, Roles.MEMBER})
    public List<GeneralCriteriaDto> getDefaultCriteriaList(@RequestParam(name = "listId", defaultValue = "1") String listId) {
        try {
            return criteriaService.getDefaultCriteria(Integer.valueOf(listId)).stream().map(GeneralCriteriaDto::new).collect(Collectors.toList());
        } catch (NumberFormatException ex) {
            throw new RuntimeException("Неверное значение параметра. Введите число");
        }
    }

    @GetMapping(value = "/diplom")
    @GakSecured(roles = Roles.PRESIDENT)
    public List<CriteriaDto> getCriteriaByDiplomId(@RequestParam(name = "diplomId") Long diplomId) {
        if(diplomId == null) throw new RuntimeException("Укажите номер диплома");
        return criteriaService.getCriteriaByDiplomId(diplomId).stream().map(CriteriaDto::new).collect(Collectors.toList());
    }


    @PostMapping(value = "/result", consumes = "application/json")
    @GakSecured(roles = {Roles.PRESIDENT})
    public void saveResultToSpeaker(@RequestBody RatingDto ratingDto) {
        this.criteriaService.saveResultToSpeaker(ratingDto.rating, ratingDto.speakerId);
    }

    @PostMapping(value = "/", consumes = "application/json")
    @GakSecured(roles = {Roles.PRESIDENT, Roles.MEMBER})
    public void saveCriteriaListWithData(@RequestBody CriteriaDtoListWithResult criteriaDtoListWithResult) {
        this.criteriaService.saveCriteriaListWithData(securityContext.getCurrentPrincipal().getId(), criteriaDtoListWithResult);
    }
}
