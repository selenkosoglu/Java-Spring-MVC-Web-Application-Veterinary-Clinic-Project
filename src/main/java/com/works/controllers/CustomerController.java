package com.works.controllers;

import com.works.entities.constant.address.Address;
import com.works.properties.CustomerInterlayer;
import com.works.properties.CustomerPetInterlayer;
import com.works.properties.PetListInterlayer;
import com.works.entities.*;
import com.works.entities.constant.address.City;
import com.works.entities.constant.address.District;
import com.works.entities.constant.pets.*;
import com.works.repositories.*;
import com.works.utils.Util;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;
import java.util.List;

@Controller
@RequestMapping("/customer")
public class CustomerController {

    final CityRepository cityRepository;
    final DistrictRepository districtRepository;
    final ColorPetRepository colorPetRepository;
    final TypePetRepository typePetRepository;
    final BreedPetRepository breedPetRepository;
    final CustomerGroupRepository customerGroupRepository;
    final CustomerRepository customerRepository;
    final PetRepository petRepository;
    final AddressRepository addressRepository;
    final JoinPetCustomerRepository joinPetCustomerRepository;
    final JoinTypeBreedPetRepository joinTypeBreedPetRepository;


    public CustomerController(CityRepository cityRepository, DistrictRepository districtRepository, ColorPetRepository colorPetRepository, TypePetRepository typePetRepository, BreedPetRepository breedPetRepository, CustomerGroupRepository customerGroupRepository, CustomerRepository customerRepository, PetRepository petRepository, AddressRepository addressRepository, JoinPetCustomerRepository joinPetCustomerRepository, JoinTypeBreedPetRepository joinTypeBreedPetRepository) {
        this.cityRepository = cityRepository;
        this.districtRepository = districtRepository;
        this.colorPetRepository = colorPetRepository;
        this.typePetRepository = typePetRepository;
        this.breedPetRepository = breedPetRepository;
        this.customerGroupRepository = customerGroupRepository;
        this.customerRepository = customerRepository;
        this.petRepository = petRepository;
        this.addressRepository = addressRepository;
        this.joinPetCustomerRepository = joinPetCustomerRepository;
        this.joinTypeBreedPetRepository = joinTypeBreedPetRepository;
    }

    @GetMapping("")
    public String customer(Model model, CustomerPetInterlayer customerPetInterlayer) {

        model.addAttribute("cities", cityRepository.findAll());
        model.addAttribute("customer", customerPetInterlayer);
        model.addAttribute("cuGroups", customerGroupRepository.findAll());

        return "customer";
    }

    @GetMapping("/getDistrict/{index}")
    @ResponseBody
    public List<District> getXDistrict(@PathVariable String index) {
        try {
            int did = Integer.parseInt(index);
            return districtRepository.getXDistricts(did);
        } catch (Exception ex) {
            Util.log("CustomerController getXDistrict Error : " + ex, this.getClass());
        }
        return null;
    }

    @GetMapping("/getColors/")
    @ResponseBody
    public List<ColorPet> getColor() {
        return colorPetRepository.findAll();
    }

    @GetMapping("/getTypes/")
    @ResponseBody
    public List<TypePet> getAllTypePet() {
        //List<TypePet> ls = typePetRepository.findAll();
        //Direkt veriyi çekmedik çünkü tüm türlerin tüm ırkları gelecek bizim seçilen türün ırklarına ihtiyacımız var.
        return typePetRepository.findAll();
    }

    @GetMapping("/getBreeds/{stIndex}")
    @ResponseBody
    public List<BreedPet> getXBreeds(@PathVariable String stIndex) {
        try {
            Integer index = Integer.parseInt(stIndex);
            return breedPetRepository.getXDistricts(index);
        } catch (Exception e) {
            Util.log("CustomerController getXBreeds Error : " + e, this.getClass());
        }
        return null;
    }

    @PostMapping("/add")
    @ResponseBody
    public Boolean addCustomer(@RequestBody @Valid CustomerPetInterlayer obj, BindingResult bindingResults) {
        if (!bindingResults.hasErrors()) {
            //CUSTOMER NESNESİNİ OLUŞTURMA
            CustomerInterlayer customerDto = obj.getCustomerObj();

            Customer customer = new Customer();
            customer.setCu_name(customerDto.getCu_name());
            customer.setCu_surname(customerDto.getCu_surname());

            System.out.println("Tel1 Format : " + Util.isTel(customerDto.getCu_tel1()));

            if (Util.isTel(customerDto.getCu_tel1())) {
                customer.setCu_tel1(customerDto.getCu_tel1());
            } else {
                return false;
            }

            System.out.println("Tel2 Format : " + Util.isTel(customerDto.getCu_tel2()));
            if (!customerDto.getCu_tel2().equals("")) {
                if (Util.isTel(customerDto.getCu_tel2())) {
                    customer.setCu_tel2(customerDto.getCu_tel2());
                } else {
                    return false;
                }
            }
            System.out.println("Email Format : " + Util.isEmail(customerDto.getCu_mail()));

            if (Util.isEmail(customerDto.getCu_mail())) {
                customer.setCu_mail(customerDto.getCu_mail());
            } else {
                Util.log("CustomerControllerError : email formatı hatalı", this.getClass());
                return false;
            }

            customer.setCu_taxname(customerDto.getCu_taxname());
            customer.setCu_note(customerDto.getCu_note());
            customer.setCu_tcnumber(customerDto.getCu_tcnumber());
            customer.setCu_rateOfDiscount(customerDto.getCu_rateOfDiscount());

            //Burası sms-mail html düzeldikten sonra değiştirilecek.
            customer.setCu_smsNotice(customerDto.getCu_smsNotice());
            customer.setCu_mailNotice(customerDto.getCu_mailNotice());

            //Address Class Add
            Address address = new Address();

            //CITY
            Integer city_id = 0;
            if (!customerDto.getCu_cities().equals("Seçim Yapınız")) {
                city_id = Integer.parseInt(customerDto.getCu_cities());
            } else {
                return false;
            }
            System.out.println("City id: " + city_id);
            Optional<City> optCity = cityRepository.findById(city_id);
            if (optCity.isPresent()) {
                address.setCity(optCity.get());
            }

            //DISTRICT
            Integer district_id = 0;
            if (!customerDto.getCu_districts().equals("Seçim Yapınız")) {
                district_id = Integer.parseInt(customerDto.getCu_districts());
            } else {
                return false;
            }
            System.out.println("İlçe IDsi:" + district_id);
            Optional<District> optDistrict = districtRepository.findById(district_id);
            if (optDistrict.isPresent()) {
                address.setDistrict(optDistrict.get());
            }

            //Address <String>
            address.setCu_address(customerDto.getCu_address());

            //Add Address to DB after add customer
            Address address1 = addressRepository.save(address);
            customer.setAddress(address1);

            //CUSTOMERGROUP
            Integer group_id = 0;
            if (!customerDto.getCu_group().equals("Seçim Yapınız")) {
                group_id = Integer.parseInt(customerDto.getCu_group());
            } else {
                return false;
            }
            System.out.println("GroupId : " + group_id);
            Optional<CustomerGroup> optGroup = customerGroupRepository.findById(group_id);
            if (optGroup.isPresent()) {
                customer.setCustomerGroup(optGroup.get());
            }
            //Eklenen customer
            customer = customerRepository.save(customer);

            //PET LİSTESİ OLUŞTURMA
            for (PetListInterlayer item : obj.getPetList()) {

                Pet pet = new Pet();

                pet.setPet_name(item.getName());
                pet.setPet_chipNumber(item.getChipNumber());
                pet.setPet_earTag(item.getEarTag());

                pet.setPet_bornDate(item.getBornDate());

                if (item.getNeutering().equals("on")) {
                    pet.setPet_neutering(true);
                } else {
                    pet.setPet_neutering(false);
                }

                if (item.getGender().equals("erkek")) {
                    pet.setPet_gender(true);
                } else {
                    pet.setPet_gender(false);
                }

                //Color
                Integer color_id = 0;
                if (!item.getColor().equals("0")) {
                    color_id = Integer.parseInt(item.getColor());
                } else {
                    return false;
                }
                System.out.println("color_id : " + color_id);
                Optional<ColorPet> optColor_pet = colorPetRepository.findById(color_id);
                if (optColor_pet.isPresent()) {
                    pet.setColorPet(optColor_pet.get());
                }

                //Irk Tür Nesnesi oluşturma
                JoinTypeBreedPet joinTypeBreedPet = new JoinTypeBreedPet();

                //Type
                Integer type_id = 0;
                if (!item.getType().equals("0")) {
                    type_id = Integer.parseInt(item.getType());
                } else {
                    return false;
                }
                System.out.println("type_id : " + type_id);
                Optional<TypePet> optType = typePetRepository.findById(type_id);
                if (optType.isPresent()) {
                    joinTypeBreedPet.setTypePet(optType.get());
                }

                //Breed
                Integer breed_id = 0;
                if (!item.getBreed().equals("0")) {
                    breed_id = Integer.parseInt(item.getBreed());
                } else {
                    return false;
                }
                System.out.println("breed_id : " + breed_id);
                Optional<BreedPet> optBreed = breedPetRepository.findById(breed_id);
                if (optBreed.isPresent()) {
                    joinTypeBreedPet.setBreedPet(optBreed.get());
                }
                joinTypeBreedPet = joinTypeBreedPetRepository.save(joinTypeBreedPet);
                pet.setJoinTypeBreedPet(joinTypeBreedPet);

                pet = petRepository.save(pet);

                JoinPetCustomer joinPetCustomer = new JoinPetCustomer();
                joinPetCustomer.setCustomer(customer);
                joinPetCustomer.setPet(pet);

                joinPetCustomerRepository.save(joinPetCustomer);
                System.out.println("Added joinPetCustomers: " + joinPetCustomer);
            }
            return true;
        } else {
            return false;
        }
    }
}