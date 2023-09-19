package com.ilionx.poc;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class PersonController {
  private final JpaPersonRepository personRepository;

  public PersonController(final JpaPersonRepository personRepository) {
    this.personRepository = personRepository;
  }

  /**
   * Method executes normally.
   */
  @GetMapping("/persons/{id}")
  public ResponseEntity<Person> getPerson(@PathVariable("id") final Long personId) {
    return ResponseEntity.ok(personRepository.findById(personId).get());
  }

  /**
   * Method executes normally.
   */
  @QueryMapping("person")
  public Person person(@Argument final Long personId) {
    return personRepository.findById(personId).get();
  }

  /**
   * Method executes normally.
   */
  @PostMapping("/persons/")
  public ResponseEntity<Person> createPerson(@RequestBody final Person person) {
    return ResponseEntity.ok(personRepository.save(person));
  }

  /**
   * This method fails.
   */
  @MutationMapping("addPerson")
  public Person addPerson(@Argument final Person person) {
    return personRepository.save(person);
  }
}
