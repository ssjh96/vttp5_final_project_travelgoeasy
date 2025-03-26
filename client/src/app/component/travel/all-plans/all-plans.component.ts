import { Component, inject } from '@angular/core';
import { TripService } from '../../../services/trip.service';
import { AuthenticationService } from '../../../services/authentication.service';
import { Trip } from '../../../model/trip';
import { Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { CountriesStore } from '../../../store/countries.store';
import { Country } from '../../../model/country';

@Component({
  selector: 'app-all-plans',
  standalone: false,
  templateUrl: './all-plans.component.html',
  styleUrl: './all-plans.component.css'
})
export class AllPlansComponent 
{
  private tripService = inject(TripService);
  private authService = inject(AuthenticationService);
  private router = inject(Router);
  private snackBar = inject(MatSnackBar);
  private countriesStore = inject(CountriesStore);

  tripsList: Trip[] = [];
  userId!: number;  
  selectedTrip!: Trip;
  countries!: Country[]

  ngOnInit(): void 
  {
    this.userId = this.authService.getUserDetails().userId;
    this.loadTrips(this.userId)
    
    this.countriesStore.countries$.subscribe(countries => {
      this.countries = countries;
      console.log('Countries loaded')
    })
  }

  protected loadTrips(userId: number): void
  {
    this.tripService.getAllTrips(userId).subscribe({
      next: (data) => { this.tripsList = data },
      error: (err) => { 
        console.error("error loading: ", err); 
      }
    })
  }

  protected confirmDelete(trip: Trip): void {
    const confirmation = confirm(`Are you sure you want to delete your trip to ${trip.destination}?`);
    if (confirmation) {
      this.onDelete(trip.tripId!);
    }
  }

  protected onDelete(tripId: number)
  {
    this.tripService.deleteTrip(tripId).subscribe({
      next: (response) => { 
        console.log("Response: " + response) 
        this.loadTrips(this.userId)
        this.snackBar.open('Trip deleted successfully', 'Close', {
          duration: 3000
        });
      },
      error: (err) => { 
        console.error("deletion error: ", err); 
        this.snackBar.open('Failed to delete trip', 'Close', {
          duration: 3000
        });
      }
    })
  }

  protected onView(tripId: number)
  {
    this.router.navigate(['/view-plan/', tripId])
  }

  protected getDestinationImage(destination: string): any 
  {
    const matchedCountry = this.countries.find(c => c.name.toLowerCase() === destination.toLowerCase());

    return matchedCountry?.pngUrl;
  }

  protected getTripMatesCount(tripMates: string): number {
    if (!tripMates || tripMates.trim() === '') {
      return 0;
    }
    return tripMates.split(',').length;
  }
}




