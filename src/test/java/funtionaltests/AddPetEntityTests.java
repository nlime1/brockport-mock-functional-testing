package funtionaltests;

import com.petstore.PetEntity;
import com.petstore.PetStoreReader;
import com.petstore.animals.attributes.Gender;
import com.petstore.animals.attributes.PetType;
import com.petstoreservices.exceptions.PetDataStoreException;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.parsing.Parser;

import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Functional tests for adding and deleting pet entities in the PetStore.
 * Utilizes REST Assured for HTTP request/response validation.
 */
public class AddPetEntityTests {

    private static Headers headers;
    private List<PetEntity> expectedResults;

    /**
     * Sets up REST Assured base URI and loads the existing pet data from file.
     * This runs before each test.
     */
    @BeforeEach
    public void retrieveDataStore() throws PetDataStoreException {
        RestAssured.baseURI = "http://localhost:8080/";
        PetStoreReader psReader = new PetStoreReader();
        expectedResults = psReader.readJsonFromFile();

        Header contentType = new Header("Content-Type", ContentType.JSON.toString());
        Header accept = new Header("Accept", ContentType.JSON.toString());
        headers = new Headers(contentType, accept);
    }

    /**
     * Functional test for adding a new cat to the pet inventory.
     * Sends a POST request and verifies the new cat appears in the updated datastore.
     */
    @TestFactory
    @DisplayName("Add Pet Entity[Cat]")
    public Stream<DynamicTest> addCatTest() throws PetDataStoreException {
        // Create a new PetEntity with randomized ID
        PetEntity newPet = new PetEntity();
        newPet.setPetId(new Random().nextInt(1000));
        newPet.setPetType(PetType.CAT);
        newPet.setCost(new BigDecimal("15.00"));
        newPet.setLegs(4);
        newPet.setGender(Gender.FEMALE);

        String uri = "inventory/petType/CAT";

        // Send POST request to add the pet
        PetEntity addedPet = given()
                .headers(headers)
                .body(newPet)
                .when()
                .post(uri)
                .then()
                .log().all()
                .assertThat().statusCode(200)
                .assertThat().contentType("application/json")
                .extract()
                .jsonPath()
                .getObject(".", PetEntity.class);

        // Reload datastore after adding pet
        PetStoreReader psReader = new PetStoreReader();
        List<PetEntity> actualResults = psReader.readJsonFromFile();

        // Generate dynamic assertions
        List<DynamicTest> testResults = Arrays.asList(
                DynamicTest.dynamicTest("Pet Added Successfully [" + addedPet.getPetId() + "]",
                        () -> assertTrue(actualResults.stream().anyMatch(p ->
                                p.getPetId() == addedPet.getPetId() &&
                                        p.getPetType() == addedPet.getPetType() &&
                                        p.getGender() == addedPet.getGender() &&
                                        p.getLegs() == addedPet.getLegs() &&
                                        p.getCost().compareTo(addedPet.getCost()) == 0
                        ))),
                DynamicTest.dynamicTest("Size of results test [" + (expectedResults.size() + 1) + "]",
                        () -> assertEquals((expectedResults.size() + 1), actualResults.size()))
        );

        return testResults.stream();
    }

    /**
     * Functional test for deleting an existing cat from the inventory.
     * Sends a DELETE request and verifies the pet is removed from the datastore.
     */
    @TestFactory
    @DisplayName("Delete Pet Entity[Cat]")
    public Stream<DynamicTest> deleteCatTest() throws PetDataStoreException {
        // Select a cat to delete
        List<PetEntity> cats = expectedResults.stream()
                .filter(p -> p.getPetType().equals(PetType.CAT))
                .sorted(Comparator.comparingInt(PetEntity::getPetId))
                .collect(Collectors.toList());

        if (cats.isEmpty()) {
            fail("There are 0 remaining cats in the inventory. Test cannot be executed");
        }

        // Pick a random cat to delete
        int index = new Random().nextInt(cats.size());
        String uri = "inventory/petType/CAT/petId/" + cats.get(index).getPetId();

        // Send DELETE request
        PetEntity deletedPet = given()
                .headers(headers)
                .when()
                .delete(uri)
                .then()
                .log().all()
                .assertThat().statusCode(200)
                .assertThat().contentType("application/json")
                .extract()
                .jsonPath()
                .getObject(".", PetEntity.class);

        // Reload datastore after deletion
        PetStoreReader psReader = new PetStoreReader();
        List<PetEntity> actualResults = psReader.readJsonFromFile();

        // Generate dynamic assertions
        List<DynamicTest> testResults = Arrays.asList(
                DynamicTest.dynamicTest("Size of results test [" + (expectedResults.size() - 1) + "]",
                        () -> assertEquals((expectedResults.size() - 1), actualResults.size())),
                DynamicTest.dynamicTest("Pet Item Not in list [" + deletedPet.getPetId() + "]",
                        () -> assertFalse(actualResults.contains(deletedPet)))
        );

        return testResults.stream();
    }
}
