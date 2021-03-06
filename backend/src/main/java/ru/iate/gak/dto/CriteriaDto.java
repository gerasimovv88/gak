package ru.iate.gak.dto;

import ru.iate.gak.model.CriteriaEntity;

public class CriteriaDto extends LongIdentifiableDto {

    public DiplomDto diplom;
    public CommissionDto commissionDto;
    public String title;
    public Integer rating;
    public String comment;

    public CriteriaDto() {}

    public CriteriaDto(CriteriaEntity criteria) {
        super(criteria.getId());
        this.diplom = new DiplomDto(criteria.getDiplom());
        this.commissionDto = new CommissionDto(criteria.getCommission());
        this.title = criteria.getTitle();
        this.rating = criteria.getRating();
        this.comment = criteria.getComment();
    }
}
