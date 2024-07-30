package vn.jobhunter.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.jobhunter.domain.Skill;
import vn.jobhunter.domain.response.ResultPaginationDTO;
import vn.jobhunter.service.SkillService;
import vn.jobhunter.util.annotation.ApiMessage;
import vn.jobhunter.util.error.IdInvalidException;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RequestMapping("/api/v1")
@RestController
public class SkillController {
    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @PostMapping("/skills")
    @ApiMessage("Create skill success")
    public ResponseEntity<Skill> createSkill(@Valid @RequestBody Skill skill) throws IdInvalidException {
        boolean isSkillNameExist = this.skillService.isSkillNameExist(skill.getName());
        if (isSkillNameExist) {
            throw new IdInvalidException("Skill " + skill.getName() + " đã tồn tại");
        }
        Skill tempSkill = this.skillService.handleCreateSkill(skill);
        return ResponseEntity.status(HttpStatus.CREATED).body(tempSkill);
    }

    @PutMapping("/skills")
    @ApiMessage("Update skill success")
    public ResponseEntity<Skill> updateSkill(@Valid @RequestBody Skill skill) throws IdInvalidException {
        Skill currentSkill = this.skillService.fetchSkillById(skill.getId());
        if (currentSkill == null) {
            throw new IdInvalidException("Skill id = " + skill.getId() + " không tồn tại");
        }

        boolean isSkillNameExist = this.skillService.isSkillNameExist(skill.getName());
        if (isSkillNameExist) {
            throw new IdInvalidException("Skill " + skill.getName() + " đã tồn tại");
        }
        currentSkill.setName(skill.getName());
        currentSkill = this.skillService.handleUpdateSkill(skill);
        return ResponseEntity.status(HttpStatus.OK).body(currentSkill);
    }

    @DeleteMapping("/skills/{id}")
    @ApiMessage("Delete a skill")
    public ResponseEntity<Void> delete(@PathVariable("id") long id) throws IdInvalidException {
        // check id
        Skill currentSkill = this.skillService.fetchSkillById(id);
        if (currentSkill == null) {
            throw new IdInvalidException("Skill id = " + id + " không tồn tại");
        }
        this.skillService.deleteSkill(id);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/skills/{id}")
    @ApiMessage("Get skill success")
    public ResponseEntity<Skill> fetchSkillById(@PathVariable("id") long id) {
        Skill tempSkill = this.skillService.fetchSkillById(id);
        return ResponseEntity.status(HttpStatus.OK).body(tempSkill);
    }

    @GetMapping("/skills")
    @ApiMessage("Get all skill success")
    public ResponseEntity<ResultPaginationDTO> fetchAllSkill(
            @Filter Specification<Skill> spec,
            Pageable pageable) {
        ResultPaginationDTO rs = this.skillService.fetchAllSkill(spec, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(rs);
    }
}