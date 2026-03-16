package dev.revature.controllers;

import dev.revature.models.Lizard;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class LizardController {

    private List<Lizard> lizards = new ArrayList<>();

    @PostMapping("/lizards")
    public ResponseEntity<Lizard> addLizard(@RequestBody Lizard lizard){
        // return status code (200)
        // return resource (lizard)
        if(lizard.getName()==null){
            return ResponseEntity.status(400).build();
        }
        lizards.add(lizard);
        return ResponseEntity.status(201).body(lizard);
    }

    @GetMapping("/lizards")
    public List<Lizard> getLizards(){
        return lizards;
    }



}
