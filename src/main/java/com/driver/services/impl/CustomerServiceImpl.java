package com.driver.services.impl;

import com.driver.model.*;
import com.driver.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.driver.repository.CustomerRepository;
import com.driver.repository.DriverRepository;
import com.driver.repository.TripBookingRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	CustomerRepository customerRepository2;

	@Autowired
	DriverRepository driverRepository2;

	@Autowired
	TripBookingRepository tripBookingRepository2;

	@Override
	public void register(Customer customer) {
		//Save the customer in database
		customerRepository2.save(customer);
	}

	@Override
	public void deleteCustomer(Integer customerId) {
		// Delete customer without using deleteById function
       Customer customer=customerRepository2.findById(customerId).get();
	   customerRepository2.delete(customer);
	}

	@Override
	public TripBooking bookTrip(int customerId, String fromLocation, String toLocation, int distanceInKm) throws Exception{
		//Book the driver with lowest driverId who is free (cab available variable is Boolean.TRUE). If no driver is available, throw "No cab available!" exception
		//Avoid using SQL query


//	   Customer customer;
//	   try{
//		   customer=customerRepository2.findById(customerId).get();
//	   }catch(Exception e){
//		   throw new Exception(e.getMessage());
//		}
////	   newTrip.setCustomer(customer);
//	   List<Driver>driverList=driverRepository2.findAll();
//	   Driver driverAvailable=null;
//	   for(Driver driver:driverList){
//		   if(driver.getCab().getAvailable()){
//			   driverAvailable=driver;
//			   break;
//		   }
//	   }
//	   if(driverAvailable==null){
//		   throw new Exception("no cab available");
//	   }
//		int ratePerKm=driverAvailable.getCab().getPerKmRate();
//	   int totalBill=ratePerKm*distanceInKm;
//		TripBooking newTrip=new TripBooking();
//	   newTrip.setFromLocation(fromLocation);
//	   newTrip.setToLocation(toLocation);
//	   newTrip.setDistanceInKm(distanceInKm);
//		newTrip.setCustomer(customer);
//		newTrip.setDriver(driverAvailable);
//		newTrip.setBill(totalBill);
//		newTrip.setStatus(TripStatus.CONFIRMED);
//		List<TripBooking>listOfTrip=customer.getTripBookingList();
//		listOfTrip.add(newTrip);
//		customer.setTripBookingList(listOfTrip);
//
//		List<TripBooking>tripBookingList=driverAvailable.getTripBookingList();
//		tripBookingList.add(newTrip);
//		driverAvailable.setTripBookingList(tripBookingList);
//		driverRepository2.save(driverAvailable);
//		customerRepository2.save(customer);
//		return newTrip;

		TripBooking newTrip=new TripBooking();
		Driver driver=null;
		//now we will find the list of drivers
		List<Driver>listOfDrivers=driverRepository2.findAll();
		//now we will iterate one by one on  driverlist
		for(Driver driver1:listOfDrivers) {
			if (driver1.getCab().getAvailable() == true) {
				if (driver == null || driver.getDriverId() > driver1.getDriverId()) {
					driver = driver1;
				}
			}
		}
		if(driver==null){
			throw new Exception("No cab available!");
		}
		Customer customer=customerRepository2.findById(customerId).get();
		newTrip.setCustomer(customer);
		newTrip.setDriver(driver);
		newTrip.setFromLocation(fromLocation);
		newTrip.setToLocation(toLocation);
		newTrip.setDistanceInKm(distanceInKm);
		newTrip.setBill(distanceInKm*10);

		driver.getCab().setAvailable(Boolean.FALSE);
		newTrip.setStatus(TripStatus.CONFIRMED);

		//adding this newTrip to customerRepository
		customer.getTripBookingList().add(newTrip);
		customerRepository2.save(customer);

		//adding this newTrip to driverRepository
		driver.getTripBookingList().add(newTrip);
		driverRepository2.save(driver);

		return newTrip;

	}

	@Override
	public void cancelTrip(Integer tripId){
		//Cancel the trip having given trip Id and update TripBooking attributes accordingly
      TripBooking tripBooking=tripBookingRepository2.findById(tripId).get();
	  tripBooking.setStatus(TripStatus.CANCELED);
	  tripBooking.setBill(0);
	  tripBooking.getDriver().getCab().setAvailable(Boolean.TRUE);
	  //cab should also be saved;
        Cab cab=tripBooking.getDriver().getCab();
	  tripBookingRepository2.save(tripBooking);
	}

	@Override
	public void completeTrip(Integer tripId){
		//Complete the trip having given trip Id and update TripBooking attributes accordingly
       TripBooking tripBooking=tripBookingRepository2.findById(tripId).get();
	   tripBooking.setStatus(TripStatus.COMPLETED);
	   int bill=tripBooking.getDriver().getCab().getPerKmRate()*tripBooking.getDistanceInKm();
	   tripBooking.setBill(bill);
	   tripBooking.getDriver().getCab().setAvailable(Boolean.TRUE);
	   tripBookingRepository2.save(tripBooking);
	}
}
