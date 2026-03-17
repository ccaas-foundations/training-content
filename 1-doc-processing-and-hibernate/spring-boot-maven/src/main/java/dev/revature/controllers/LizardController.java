package dev.revature.controllers;

import dev.revature.models.Lizard;
import org.springframework.http.MediaType;
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

    public LizardController(){
        lizards.add(new Lizard(25, "Harold","blue" ,3));
        lizards.add(new Lizard(26, "Jane","blue" ,2.8));

    }

    //updating the consumes and produces attributes allow us to configure the request/response body format
    @PostMapping(value = "/lizards",consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE) //producing = response format, consuming = request format
    // previously, we used the default values for produces and consumes, which provided us with JSON (explicitly declared below)
    //@PostMapping(value = "/lizards",consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Lizard> addLizard(@RequestBody Lizard lizard){
        // return status code (200)
        // return resource (lizard)
        if(lizard.getName()==null){
            return ResponseEntity.status(400).build();
        }
        lizards.add(lizard);
        return ResponseEntity.status(201).body(lizard);
    }

    @GetMapping(value = "/lizards",produces = MediaType.APPLICATION_XML_VALUE) //producing xml
    public List<Lizard> getLizards(){
        return lizards;
    }



}
