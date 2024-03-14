package services;

import com.petstore.AnimalType;
import com.petstore.PetEntity;

import com.petstore.animals.CatEntity;
import com.petstore.animals.DogEntity;
import com.petstore.animals.attributes.Breed;
import com.petstore.animals.attributes.Gender;
import com.petstore.animals.attributes.PetType;
import com.petstore.animals.attributes.Skin;
import com.petstore.exceptions.DuplicatePetStoreRecordException;
import com.petstore.exceptions.PetNotFoundSaleException;
import com.petstore.exceptions.PetTypeNotSupportedException;
import com.petstoreservices.exceptions.PetDataStoreException;
import com.petstoreservices.exceptions.PetInventoryFileNotCreatedException;
import com.petstoreservices.exceptions.PetStoreAnimalTypeException;
import com.petstoreservices.repository.PetRepository;
import com.petstoreservices.service.PetInventoryService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.Mockito;

import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.petstore.animals.attributes.Skin.FUR;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PetStoreServiceTests
{
    @InjectMocks
    private PetInventoryService petService;

    @Mock //Mock the petRepository
    private PetRepository petRepository;

    private List<PetEntity> myPets;


    @BeforeEach
    public void init()
    {
        myPets = new ArrayList<PetEntity>(Arrays.asList(
                new DogEntity(AnimalType.DOMESTIC, FUR, Gender.MALE, Breed.MALTESE,
                        new BigDecimal("750.00"), 3),
                new DogEntity(AnimalType.DOMESTIC, FUR, Gender.MALE, Breed.POODLE,
                        new BigDecimal("650.00"), 1),
                new DogEntity(AnimalType.DOMESTIC, FUR, Gender.FEMALE, Breed.GREY_HOUND,
                        new BigDecimal("750.00"), 4),
                new CatEntity(AnimalType.DOMESTIC, Skin.HAIR, Gender.MALE, Breed.BURMESE,
                        new BigDecimal("65.00"),1)
        ));

    }

    @TestFactory
    @Order(1)
    @DisplayName("Validate Dogs only return test")
    public Stream<DynamicTest> getInventoryTestByDog() throws PetNotFoundSaleException, PetDataStoreException
    {
        Mockito.lenient().doReturn(myPets).when(petRepository).getPetInventory(); //mock the getInventory Repo
        List<PetEntity> foundPetList = petService.getPetsByPetType(PetType.DOG);  //retrieve the data
        List<DynamicTest> inventoryTests = Arrays.asList(
                DynamicTest.dynamicTest("List size test",
                        ()-> assertEquals(3, foundPetList.size())),
                DynamicTest.dynamicTest("Pet item with Dog id 1",
                        ()-> assertTrue(foundPetList.stream()
                                .anyMatch(c -> c.getPetId()==1 && c.getPetType() == PetType.DOG
                                        && c.getGender() ==Gender.MALE && c.getBreed() == Breed.POODLE))),
                DynamicTest.dynamicTest("Pet item with Dog id 3",
                        ()-> assertTrue(foundPetList.stream()
                                .anyMatch(c -> c.getPetId()== 3&& c.getPetType() == PetType.DOG
                                        && c.getGender() ==Gender.MALE && c.getBreed() == Breed.MALTESE))),
                DynamicTest.dynamicTest("Pet item with Cat id 1 Not Found",
                        ()-> assertFalse(foundPetList.contains(myPets.get(3)))));
        verify(petRepository).getPetInventory();
        return inventoryTests.stream();
    }

    @TestFactory
    @Order(2)
    @DisplayName("Delete Pet Item<Dog> from inventory test")
    public Stream<DynamicTest> removePetItem() throws  PetInventoryFileNotCreatedException,
            DuplicatePetStoreRecordException, PetNotFoundSaleException,  PetDataStoreException
    {
        PetEntity removedPetItem = myPets.stream()
                .filter(p -> p.getPetType().equals(PetType.DOG) && p.getPetId() == 3)
                .findFirst()
                .orElse(null); //capture the item from the existing list in myPets

        Mockito.lenient().doReturn(myPets).when(petRepository).getPetInventory(); //mock the getInventory Repo

        //mock the removeEntity() repo and define the results
        Mockito.lenient().doReturn(removedPetItem).when(this.petRepository).removeEntity(removedPetItem);

        //Execute the service and return the removeEntity id
        PetEntity removeEntity = petService.removeInventoryByIDAndPetType(PetType.DOG, removedPetItem.getPetId());


        List<DynamicTest> removedInventoryTests = Arrays.asList(
                DynamicTest.dynamicTest("Pet item with Dog id [3]",
                        ()-> assertEquals(3, removeEntity.getPetId())),
                DynamicTest.dynamicTest("Dog breed [" + AnimalType.DOMESTIC + "]",
                        ()-> assertSame(AnimalType.DOMESTIC,  removeEntity.getAnimalType())),
                DynamicTest.dynamicTest("Dog Gender[" + Gender.MALE + "]",
                        ()-> assertSame(Gender.MALE, removeEntity.getGender())));
        return removedInventoryTests.stream();
    }

    @TestFactory
    @Order(3)
    @DisplayName("Validate Add DOG POST Test")
    public Stream<DynamicTest> postPetTest() throws
            PetInventoryFileNotCreatedException, PetStoreAnimalTypeException,
            PetTypeNotSupportedException, PetDataStoreException
    {
        Mockito.doReturn(myPets).when(petRepository).getPetInventory(); //mock the getInventory Repo

        PetEntity newDogItem = new DogEntity(AnimalType.DOMESTIC, FUR, Gender.FEMALE, Breed.GERMAN_SHEPHERD,
                new BigDecimal("225.00"), 5);

        List<PetEntity> sortedPets = myPets.stream()
                .filter(p -> p.getPetType().equals(PetType.DOG))
                .sorted(Comparator.comparingInt(PetEntity::getPetId))
                .collect(Collectors.toList());

        Mockito.lenient().doReturn(newDogItem).when(this.petRepository).createPetEntity(newDogItem,sortedPets);

        PetEntity aEntity = this.petService.addInventory(PetType.DOG, newDogItem);
       // System.out.println("say size" + sortedPets.size());
        System.out.println("say what" + aEntity.getPetType());
        List<DynamicTest> inventoryTests = Arrays.asList(
                DynamicTest.dynamicTest("Pet item with Dog id 2",
                        ()-> assertEquals(5, aEntity.getPetId())),
                DynamicTest.dynamicTest("Dog breed",
                        ()-> assertSame(AnimalType.DOMESTIC, aEntity.getAnimalType())),
                DynamicTest.dynamicTest("Dog Gender",
                        ()-> assertSame(Gender.FEMALE, aEntity.getGender())));
        //verify(petRepository, times(1)).getPetInventory();

        return inventoryTests.stream();
    }
}
