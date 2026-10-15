package com.workintech.s17d2.rest;

import com.workintech.s17d2.model.Developer;
import com.workintech.s17d2.model.Experience;
import com.workintech.s17d2.model.JuniorDeveloper;
import com.workintech.s17d2.model.MidDeveloper;
import com.workintech.s17d2.model.SeniorDeveloper;
import com.workintech.s17d2.tax.Taxable;

import jakarta.annotation.PostConstruct;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/developers")
public class DeveloperController {

    public Map<Integer, Developer> developers;

    private final Taxable developerTax;

    public DeveloperController(Taxable developerTax) {
        this.developerTax = developerTax;
    }

    @PostConstruct
    public void init() {
        developers = new HashMap<>();
    }

    @GetMapping
    public List<Developer> getAllDevelopers() {
        return new ArrayList<>(developers.values());
    }

    @GetMapping("/{id}")
    public Developer getDeveloperById(@PathVariable Integer id) {
        Developer developer = developers.get(id);

        if (developer == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Developer bulunamadı");
        }

        return developer;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Developer addDeveloper(@RequestBody Developer request) {
        Developer developer;

        double salary = request.getSalary();

        if (request.getExperience() == Experience.JUNIOR) {
            salary -= salary * developerTax.getSimpleTaxRate() / 100.0;
            developer = new JuniorDeveloper(
                    request.getId(), request.getName(), salary);

        } else if (request.getExperience() == Experience.MID) {
            salary -= salary * developerTax.getMiddleTaxRate() / 100.0;
            developer = new MidDeveloper(
                    request.getId(), request.getName(), salary);

        } else if (request.getExperience() == Experience.SENIOR) {
            salary -= salary * developerTax.getUpperTaxRate() / 100.0;
            developer = new SeniorDeveloper(
                    request.getId(), request.getName(), salary);

        } else {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Geçersiz deneyim seviyesi");
        }

        developers.put(developer.getId(), developer);
        return developer;
    }

    @PutMapping("/{id}")
    public Developer updateDeveloper(
            @PathVariable Integer id,
            @RequestBody Developer request) {

        if (!developers.containsKey(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Developer bulunamadı");
        }

        Developer updatedDeveloper;
        double salary = request.getSalary();

        if (request.getExperience() == Experience.JUNIOR) {
            salary -= salary * developerTax.getSimpleTaxRate() / 100.0;
            updatedDeveloper = new JuniorDeveloper(
                    id, request.getName(), salary);

        } else if (request.getExperience() == Experience.MID) {
            salary -= salary * developerTax.getMiddleTaxRate() / 100.0;
            updatedDeveloper = new MidDeveloper(
                    id, request.getName(), salary);

        } else if (request.getExperience() == Experience.SENIOR) {
            salary -= salary * developerTax.getUpperTaxRate() / 100.0;
            updatedDeveloper = new SeniorDeveloper(
                    id, request.getName(), salary);

        } else {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Geçersiz deneyim seviyesi");
        }

        developers.put(id, updatedDeveloper);
        return updatedDeveloper;
    }

    @DeleteMapping("/{id}")
    public Map<String, String> deleteDeveloper(@PathVariable Integer id) {
        if (developers.remove(id) == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Developer bulunamadı");
        }

        return Map.of("message", "Developer silindi");
    }
}