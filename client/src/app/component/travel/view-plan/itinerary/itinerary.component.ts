// itinerary.component.ts - Angular Material version
import { Component, inject, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';
import { MatSnackBar } from '@angular/material/snack-bar';

import { MapService } from '../../../../services/map.service';
import { TripService } from '../../../../services/trip.service';
import { PlaceService } from '../../../../services/place.service';
import { Trip } from '../../../../model/trip';
import { CountriesStore } from '../../../../store/countries.store';
import { PlannedPlace } from '../../../../model/place';
import { GeminiService } from '../../../../services/gemini.service';
import { AuthenticationService } from '../../../../services/authentication.service';

@Component({
  selector: 'app-itinerary',
  standalone: false,
  templateUrl: './itinerary.component.html',
  styleUrls: ['./itinerary.component.css']
})
export class ItineraryComponent implements OnInit {
  
  private mapSvc = inject(MapService);
  private activatedRoute = inject(ActivatedRoute);
  private tripService = inject(TripService);
  private placeService = inject(PlaceService);
  private snackBar = inject(MatSnackBar);
  private countriesStore = inject(CountriesStore);
  private geminiService = inject(GeminiService);
  private authService = inject(AuthenticationService);
  
  tripId!: number;
  trip: Trip = { 
                tripId: 0, 
                userId: 0, 
                tripName: "", 
                destination: "", 
                startDate: "", 
                endDate: "", 
                tripMates: "" 
                };
  
  countryCode: string = 'SG';
  planPlaces: PlannedPlace[] = [];
  days: number[] = []; 
  selectedDay: number = 1;
  selectedDayIndex: number = 0; 
  autoComplete!: google.maps.places.Autocomplete;
  isGenerating: boolean = false;
  
  
  ngOnInit(): void 
  {
    this.tripId = Number(this.activatedRoute.snapshot.paramMap.get('tripId'));
    this.loadTripData();
    this.loadItinerary();
  }
  

  // Handle the day selection from mat-tab-group
  protected onDaySelected(index: number): void {
    this.selectedDay = this.days[index];
  }
  
  private async initGooglePlaces(): Promise<void> 
  {
    await google.maps.importLibrary("places");
    this.autoComplete = new google.maps.places.Autocomplete(
      document.getElementById('autocomplete') as HTMLInputElement, 
      {
        componentRestrictions: {country: this.countryCode}, // return certain place results
        fields: ['name', 'formatted_address', 'geometry'] // filtering
      }
    );

    this.autoComplete.addListener('place_changed', () => this.placeChanged());
  }
  
  // handle new place selection
  private placeChanged(): void {
    const place = this.autoComplete.getPlace();

    console.log("Google Place Data: ", place)

    if (place.geometry?.location) {
      const lat = place.geometry.location.lat();
      const lng = place.geometry.location.lng();
      
      // add to places array
      this.planPlaces.push({
        id: this.planPlaces.length,
        name: place.name,
        address: place.formatted_address,
        lat: lat,
        lng: lng,
        day: this.selectedDay,
        orderIndex: this.getPlacesForDay(this.selectedDay).length
      });
      
      // update map service
      this.mapSvc.addPlace({
        lat, lng, 
        day: this.selectedDay,
        name: place.name,
        address: place.formatted_address
      });
      
      // clear input
      (document.getElementById('autocomplete') as HTMLInputElement).value = '';
      
      // notify user
      this.snackBar.open(`Added ${place.name} to Day ${this.selectedDay}`, 'OK', { 
        duration: 2000 
      });
    }
  }

  // Generate itinerary using Gemini AI
  protected generateItinerary(): void 
  {
    const isPro = this.authService.getUserDetails().isPro
    if(!isPro)
    {
      this.snackBar.open('Please upgrade to PRO status to use AI features', 'OK', { duration: 3000 });
      return;
    }
    this.isGenerating = true;
    const daysCount = this.days.length;
    
    this.snackBar.open(`Generating ${daysCount}-day itinerary for ${this.trip.destination}...`, 'OK', { duration: 3000 });
    
    this.geminiService.generateItinerary(this.trip.destination, daysCount, this.trip.startDate, this.tripId)
      .subscribe(
      {
        next: (response) => 
        {
          console.log("Gemini Resp: ", response)
          this.processGeminiResponse(response);
          this.isGenerating = false;
        },
        error: (err) => 
        {
          console.error('Error generating itinerary:', err);
          this.snackBar.open('Failed to generate itinerary. Please try again.', 'OK', { duration: 3000 });
          this.isGenerating = false;
        }
      }
    );
  }

  // Process Gemini response and add suggested places to itinerary
  private processGeminiResponse(response: string): void 
  {
    // clear current places first
    this.planPlaces = [];
    
    // Parse the JSON response from Gemini
    let suggestions: any[] = [];

    const cleanedResponse = response.replace(/```json|```/g, '').trim();
    console.log("cleanResponse: ", cleanedResponse)
    suggestions = JSON.parse(cleanedResponse);
    
    // process suggestions for each day
    let placesAdded = 0;
    suggestions.forEach((dayPlan) => 
    {
      const day = dayPlan.day;
      const dayPlaces = dayPlan.places;
      
      // Add each place to our places array
      dayPlaces.forEach((place: any, index: number) => 
      {
        this.planPlaces.push(
        {
          id: this.planPlaces.length, // Generate unique ID
          name: place.name,
          address: place.address || `${place.name}, ${this.trip.destination}`,
          lat: place.lat,
          lng: place.lng,
          day: day,
          orderIndex: index
        });
        placesAdded++;
      });
    });
    
    // update map with all the new places
    const mapPlaces = this.planPlaces.map(place => ({
      lat: place.lat,
      lng: place.lng,
      day: place.day,
      name: place.name,
      address: place.address
    }));
    
    this.mapSvc.updatePlaces(mapPlaces);
    
    this.snackBar.open(`Generated itinerary with ${placesAdded} attractions for ${this.trip.destination}`, 'OK', { 
      duration: 3000 
    });  
  }
  
  private loadTripData(): void {
    this.tripService.getTripById(this.tripId).subscribe({
      next: (data) => {
        this.trip = data;

        this.countriesStore.countries$.subscribe(countries => 
        {
          const matchedCountry = countries.find(c => c.name.toLowerCase() === this.trip.destination.toLowerCase());
          
          if (matchedCountry) {
            this.countryCode = matchedCountry.code; 
            console.log(`Country code for ${this.trip.destination}: ${this.countryCode}`);
          } else {
            console.warn(`No country code found for destination: ${this.trip.destination}`);
          }
        });

        const startDate = new Date(data.startDate)
        const endDate = new Date (data.endDate)

        const daysDiff = Math.ceil((endDate.getTime() - startDate.getTime()) / (1000 * 3600 * 24)) + 1; // +1 to include the first day

        console.log(`Trip from ${startDate.toDateString()} to ${endDate.toDateString()} - ${daysDiff} days`);

        for (let i = 1; i <= daysDiff; i++) {
          this.days.push(i);
        }

        this.initGooglePlaces(); // init after load trip to put the country code for filtering
      },
      error: (err) => {
        console.error('Failed to load trip details:', err);
        this.snackBar.open('Failed to load trip details', 'OK', { duration: 3000 });
      }
    });
  }
  
private loadItinerary(): void {
  this.placeService.getItineraryByTripId(this.tripId).subscribe({
    next: (places) => {
      if (places && places.length > 0) {
        // Clear existing places array
        this.planPlaces = [];
        
        // Map all the backend data to plannedPlaces format
        places.forEach((place, index) => {
          this.planPlaces.push({
            id: index,
            name: place.placeName,
            address: place.address,
            lat: place.latitude,
            lng: place.longitude,
            day: place.dayNumber,
            orderIndex: place.orderIndex
          });
        });
        
        // Update map service with all places
        const mapPlaces = this.planPlaces.map(place => ({
          lat: place.lat,
          lng: place.lng,
          day: place.day,
          name: place.name,
          address: place.address
        }));
        
        this.mapSvc.updatePlaces(mapPlaces);
        
        console.log(`Loaded ${this.planPlaces.length} places for itinerary`);
      }
    },
    error: (err) => {
      console.log('No existing itinerary found or error loading itinerary', err);
    }
  });
}
  
  protected saveItinerary(): void {
    if (this.planPlaces.length === 0) {
      this.snackBar.open('No places to save', 'OK', { duration: 3000 });
      return;
    }
    
    const placesToSave = this.mapSvc.convertToBackendFormat(this.planPlaces, this.tripId);
    
    this.placeService.saveItinerary(this.tripId, placesToSave).subscribe({
      next: () => this.snackBar.open('Itinerary saved successfully', 'OK', { duration: 3000
      }),
      error: (err) => {
        this.snackBar.open('Failed to save: ' + err.message, 'OK', { duration: 3000
      })
      console.error(err.message)
    }
    });
  }
  
  protected getPlacesForDay(day: number): PlannedPlace[] {
    return this.planPlaces
      .filter(place => place.day === day)
      .sort((a, b) => a.orderIndex - b.orderIndex); 
  }
  
  protected getFormattedDate(day: number): string 
  {
    const startDate = new Date(this.trip.startDate);
    const date = new Date(startDate);
    date.setDate(startDate.getDate() + day - 1); // -1 because day 1 is the start date itself
    
    return date.toLocaleDateString('en-US', { 
      weekday: 'long', month: 'short', day: 'numeric' 
    });
  }
  
  removePlace(place: PlannedPlace): void {
    const index = this.planPlaces.findIndex(p => p.id === place.id);
    const placeName = place.name;

    // Remove from array
    this.planPlaces.splice(index, 1);
      
    // Update order indices
    this.planPlaces
      .filter(p => p.day === place.day)
      .forEach((p, i) => { p.orderIndex = i; });
    
    // Update map
    this.mapSvc.updatePlaces(this.planPlaces.map(p => ({
      lat: p.lat,
      lng: p.lng,
      day: p.day,
      name: p.name,
      address: p.address
    })));
    
    this.snackBar.open(`Removed ${placeName} from itinerary`, 'OK', { duration: 3000 });
  }
}