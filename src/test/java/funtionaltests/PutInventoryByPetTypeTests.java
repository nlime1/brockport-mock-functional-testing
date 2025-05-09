package funtionaltests;

import com.petstore.AnimalType;
import com.petstore.PetEntity;
import com.petstore.PetStoreReader;
import com.petstore.animals.CatEntity;
import com.petstore.animals.DogEntity;
import com.petstore.animals.attributes.Breed;
import com.petstore.animals.attributes.Gender;
import com.petstore.animals.attributes.PetType;
import com.petstore.animals.attributes.Skin;
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
 * Functional tests for the PUT /inventory/update endpoint in the Pet Store API.
 *
 * These tests validate:
 * - Updating a pet's cost (DOG)
 * - Updating multiple fields for a pet (CAT)
 * - Handling update attempts on non-existent pets
 *
 * Uses RestAssured for HTTP requests and JUnit 5 for assertions and dynamic test generation.
 */
public class PutInventoryByPetTypeTests {

    // Common HTTP headers used in all tests
    private static Headers headers;

    // The expected results loaded from a JSON data store before each test
    private List<PetEntity> expectedResults;

    /**
     * Load the pet data from the JSON file and prepare HTTP headers before each test runs.
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
     * Tests updating only the cost of a DOG pet using PUT request.
     * Confirms that the response and the updated data in the data store reflect the change.
     */
    @TestFactory
    @DisplayName("Update Pet Cost[DOG]")
    public Stream<DynamicTest> updatePetCostTest() throws PetDataStoreException {
        List<PetEntity> dogs = expectedResults.stream()
                .filter(p -> p.getPetType().equals(PetType.DOG))
                .collect(Collectors.toList());

        if (dogs.isEmpty()) {
            fail("There are 0 dogs in the inventory. Test cannot be executed");
        }

        PetEntity testDog = dogs.get(0);
        BigDecimal newCost = new BigDecimal("999.99");

        // Only the cost is being updated
        PetEntity updateRequest = new PetEntity();
        updateRequest.setCost(newCost);

        String uri = "inventory/update?petType=DOG&petId=" + testDog.getPetId();

        // Send PUT request and parse the updated pet response
        PetEntity updatedPet = given()
                .headers(headers)
                .body(updateRequest)
                .when()
                .put(uri)
                .then()
                .log().all()
                .assertThat().statusCode(200)
                .assertThat().contentType("application/json")
                .extract()
                .jsonPath()
                .getObject(".", PetEntity.class);

        // Reload data from JSON to verify persistence
        PetStoreReader psReader = new PetStoreReader();
        List<PetEntity> actualResults = psReader.readJsonFromFile();

        PetEntity fetchedPet = actualResults.stream()
                .filter(p -> p.getPetId() == testDog.getPetId() && p.getPetType().equals(PetType.DOG))
                .findFirst()
                .orElse(null);

        // Return multiple assertions as dynamic tests
        List<DynamicTest> testResults = Arrays.asList(
                DynamicTest.dynamicTest("Updated Cost in Response [" + newCost + "]",
                        () -> assertEquals(newCost.doubleValue(), updatedPet.getCost().doubleValue(), 0.001)),
                DynamicTest.dynamicTest("Updated Pet ID unchanged [" + testDog.getPetId() + "]",
                        () -> assertEquals(testDog.getPetId(), updatedPet.getPetId())),
                DynamicTest.dynamicTest("Updated Pet Type unchanged [" + testDog.getPetType() + "]",
                        () -> assertEquals(testDog.getPetType(), updatedPet.getPetType())),
                DynamicTest.dynamicTest("Update persisted in data store",
                        () -> assertNotNull(fetchedPet)),
                DynamicTest.dynamicTest("Updated Cost persisted in data store [" + newCost + "]",
                        () -> assertEquals(newCost.doubleValue(), fetchedPet.getCost().doubleValue(), 0.001))
        );

        return testResults.stream();
    }

    /**
     * Tests updating multiple fields of a CAT pet.
     * Validates both the response and persisted data.
     */
    @TestFactory
    @DisplayName("Update Multiple Pet Fields[CAT]")
    public Stream<DynamicTest> updateMultiplePetFieldsTest() throws PetDataStoreException {
        List<PetEntity> cats = expectedResults.stream()
                .filter(p -> p.getPetType().equals(PetType.CAT))
                .collect(Collectors.toList());

        if (cats.isEmpty()) {
            fail("There are 0 cats in the inventory. Test cannot be executed");
        }

        PetEntity testCat = cats.get(0);

        // Update multiple attributes
        PetEntity updateRequest = new PetEntity();
        updateRequest.setCost(new BigDecimal("888.88"));
        updateRequest.setGender(Gender.MALE);
        updateRequest.setLegs(4);
        updateRequest.setBreed(Breed.SIAMESE);

        String uri = "inventory/update?petType=CAT&petId=" + testCat.getPetId();

        PetEntity updatedPet = given()
                .headers(headers)
                .body(updateRequest)
                .when()
                .put(uri)
                .then()
                .log().all()
                .assertThat().statusCode(200)
                .assertThat().contentType("application/json")
                .extract()
                .jsonPath()
                .getObject(".", PetEntity.class);

        PetStoreReader psReader = new PetStoreReader();
        List<PetEntity> actualResults = psReader.readJsonFromFile();

        PetEntity fetchedPet = actualResults.stream()
                .filter(p -> p.getPetId() == testCat.getPetId() && p.getPetType().equals(PetType.CAT))
                .findFirst()
                .orElse(null);

        List<DynamicTest> testResults = Arrays.asList(
                DynamicTest.dynamicTest("Updated Cost in Response",
                        () -> assertEquals(updateRequest.getCost().doubleValue(), updatedPet.getCost().doubleValue(), 0.001)),
                DynamicTest.dynamicTest("Updated Gender in Response",
                        () -> assertEquals(updateRequest.getGender(), updatedPet.getGender())),
                DynamicTest.dynamicTest("Updated Legs in Response",
                        () -> assertEquals(updateRequest.getLegs(), updatedPet.getLegs())),
                DynamicTest.dynamicTest("Update persisted in data store",
                        () -> assertNotNull(fetchedPet)),
                DynamicTest.dynamicTest("All updates persisted in data store",
                        () -> {
                            assertEquals(updateRequest.getCost().doubleValue(), fetchedPet.getCost().doubleValue(), 0.001);
                            assertEquals(updateRequest.getGender(), fetchedPet.getGender());
                            assertEquals(updateRequest.getLegs(), fetchedPet.getLegs());
                        })
        );

        return testResults.stream();
    }

    /**
     * Tests the API's behavior when attempting to update a non-existent pet.
     * Expects a 404 Not Found error and validates the error message structure.
     */
    @TestFactory
    @DisplayName("Update Non-existent Pet")
    public Stream<DynamicTest> putNonExistentPetEntityTest() {
        RestAssured.registerParser("application/json", Parser.JSON);

        // Try to update a pet that doesn't exist (ID 999)
        BadRequestResponseBody body = given()
                .headers(headers)
                .and()
                .body(new CatEntity(AnimalType.DOMESTIC, Skin.UNKNOWN, Gender.FEMALE, Breed.SPHYNX,
                        new BigDecimal("225.00")))
                .when()
                .put("inventory/update/petType/999")
                .then()
                .log().all()
                .assertThat().statusCode(404)
                .assertThat().contentType("application/json")
                .extract()
                .jsonPath()
                .getObject(".", BadRequestResponseBody.class);

        // Return assertions defined by BadRequestResponseBody
        return body.executeTests("Not Found", "No static resource inventory/update/petType/999.",
                "/inventory/update/petType/999", 404).stream();
    }
}
