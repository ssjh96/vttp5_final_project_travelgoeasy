import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { TripService } from '../../../services/trip.service';
import { PlaceService } from '../../../services/place.service';
import { Trip } from '../../../model/trip';
import { Place } from '../../../model/place';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Clipboard } from '@angular/cdk/clipboard';

interface DayGroup {
  day: number;
  places: Place[];
}

@Component({
  selector: 'app-shared-itinerary',
  standalone: false,
  templateUrl: './shared-itinerary.component.html',
  styleUrl: './shared-itinerary.component.css'
})
export class SharedItineraryComponent implements OnInit
{
  private route = inject(ActivatedRoute)
  private tripSvc = inject (TripService)
  private placeSvc = inject (PlaceService)
  private snackBar = inject(MatSnackBar);
  private clipboard = inject(Clipboard);
  
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

  itinerary: Place[] = []; // initial empty array
  dayGroups: DayGroup[] = [];

  // success & error
  errorMessage: string = '';
  // successMessage: string = '';

  ngOnInit(): void {
    this.tripId = Number(this.route.snapshot.paramMap.get('tripId'));
    this.loadTripData();
  }

  private loadTripData(): void {
    this.tripSvc.getTripById(this.tripId).subscribe({
      next: (tripData) => {
        console.log("tripData: ", tripData);
        this.trip = tripData
        this.loadItinerary();
        // this.successMessage = "Trip succesfully loaded!"
      },
      error: (err) => {
        this.errorMessage = "Trip not found..";
        console.error(err.message);
      }
    })
  }

  private loadItinerary(): void {
    this.placeSvc.getItineraryByTripId(this.tripId).subscribe({
      next: (places) => {
        this.itinerary = places;
        this.organizeItineraryByDay();
        // this.successMessage = "Itinerary succesfully loaded!"
      },
      error: (err) => {
        // this.errorMessage = "Unable to load itinerary..";
        console.error(err.message);
      }
    })
  }

  protected formatDayDate(startDateString: string, daysToAdd: number = 0): string 
  { 
    const date = new Date(startDateString);
    date.setDate(date.getDate() + daysToAdd); // day 1 - 1 = 0, for day 1 days to add is 0
    
    return date.toLocaleDateString('en-US', 
    { 
      weekday: 'long', 
      month: 'short', 
      day: 'numeric'
    });
  }

  private organizeItineraryByDay(): void {

    // const days = this.itinerary
    //   .map(place => place.dayNumber)
    //   .sort((a, b) => a - b);

    // Get unique days 
    const days = [...new Set(this.itinerary
      .map(place => place.dayNumber)
    )]
    .sort((a, b) => a - b);

    console.log("days: ", days)
    
    // Create day groups
    this.dayGroups = days.map(day => 
    {
      return {day, 
              places: this.itinerary
                      .filter(place => place.dayNumber === day)
                      .sort((a, b) => a.orderIndex - b.orderIndex)
      };
    });
  }

  protected getTripMatesCount(tripMates?: string): number {
    if (!tripMates || tripMates.trim() === '') {
      return 0;
    }
    return tripMates.split(',').length;
  }

  protected copyShareLink(): void {
    const url = window.location.href;
    this.clipboard.copy(url);
    
    this.snackBar.open('Share link copied to clipboard!', 'Close', { duration: 3000 });
  }


}
