package vn.jobhunter.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.jobhunter.domain.Company;
import vn.jobhunter.domain.Skill;
import vn.jobhunter.domain.response.ResultPaginationDTO;
import vn.jobhunter.repository.SkillRepository;

@Service
public class SkillService {
    private final SkillRepository skillRepository;

    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    public Skill handleCreateSkill(Skill skill) {
        return this.skillRepository.save(skill);
    }

    public boolean isSkillNameExist(String reqSkillName) {
        return this.skillRepository.existsByName(reqSkillName);
    }

    public Skill handleUpdateSkill(Skill reqSkill) {
        Optional<Skill> tempSkill = this.skillRepository.findById(reqSkill.getId());

        if (tempSkill.isPresent()) {
            Skill currentSkill = tempSkill.get();
            currentSkill.setName(reqSkill.getName());

            return this.skillRepository.save(currentSkill);
        }
        return null;
    }

    public Skill fetchSkillById(long id) {
        Optional<Skill> skillOptional = this.skillRepository.findById(id);

        if (skillOptional.isPresent()) {
            return skillOptional.get();
        }
        return null;
    }

    public ResultPaginationDTO fetchAllSkill(Specification<Skill> spec, Pageable pageable) {
        Page<Skill> pageSkill = this.skillRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageSkill.getNumber() + 1);
        mt.setPageSize(pageSkill.getSize());

        mt.setPages(pageSkill.getTotalPages());
        mt.setTotal(pageSkill.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(pageSkill.getContent());

        return rs;
    }

    public void deleteSkill(long id) {
        // delete job (inside job_skill table)
        Optional<Skill> skillOptional = this.skillRepository.findById(id);
        Skill currentSkill = skillOptional.get();
        currentSkill.getJobs().forEach(job -> job.getSkills().remove(currentSkill));

        // delete skill
        this.skillRepository.delete(currentSkill);
    }
}
