import { Component, inject, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { TripService } from '../../../services/trip.service';
import { Trip } from '../../../model/trip';
import { ActivatedRoute, Router } from '@angular/router';
import { Clipboard } from '@angular/cdk/clipboard';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-view-plan',
  standalone: false,
  templateUrl: './view-plan.component.html',
  styleUrls: ['./view-plan.component.css']
})
export class ViewPlanComponent implements OnInit 
{
  private tripService = inject(TripService);
  private activatedRoute = inject(ActivatedRoute);
  private http = inject(HttpClient);
  private clipboard = inject(Clipboard);
  private snackBar = inject(MatSnackBar);
  private router = inject(Router);
  
  // Trip information
  tripId!: number;
  selectedTrip: Trip = { 
    tripId: 0, 
    userId: 0, 
    tripName: "", 
    destination: "", 
    startDate: "", 
    endDate: "", 
    tripMates: "" 
  };

  // Loading states
  isLoading: boolean = true;

  selectedDestination: string = "";
  tripDuration: number = 0;
  tripMatesArray: string[] = [];
  
  // Display properties
  imageUrl: string = '';
  errorMessage: string = '';

  ngOnInit(): void {
    // Extract tripId from route parameters
    this.tripId = Number(this.activatedRoute.snapshot.paramMap.get('tripId'));
    this.loadTripDetails(this.tripId);
    console.log("View Plan component received tripId:", this.tripId);
  }

  private loadTripDetails(tripId: number): void {
    this.errorMessage = '';
    this.isLoading = true;
    
    this.tripService.getTripById(tripId).subscribe({
      next: (tripInfo) => {
        console.log("view-plan - tripInfo:", tripInfo);
        this.selectedTrip = tripInfo;
        this.selectedDestination = tripInfo.destination;

        // Calculate trip duration
        this.calculateTripDuration();

        // process trip mates if any
        this.processTripMates();
        
        // Load background image
        if (tripInfo.destination) 
        {
          this.fetchRandomImage(tripInfo.destination);
        }
      },
      error: (err) => { 
        console.error("Error loading trip details:", err);
        this.errorMessage = "Failed to load trip details. Please try again.";
        this.isLoading = false;
      }
    });
  }

  private calculateTripDuration(): void 
  {  
    const startDate = new Date(this.selectedTrip.startDate);
    const endDate = new Date(this.selectedTrip.endDate);
    const timeDiff = endDate.getTime() - startDate.getTime();
    this.tripDuration = Math.ceil(timeDiff / (1000 * 3600 * 24)) + 1; // +1 to include both start and end days
  }

  private processTripMates(): void 
  {
    if (this.selectedTrip.tripMates && this.selectedTrip.tripMates.trim() !== '') 
    {
      this.tripMatesArray = this.selectedTrip.tripMates
        .split(',')
        .map(email => email.trim())
        .filter(email => email !== '');
    } 
    else 
    {
      this.tripMatesArray = [];
    }
  }

  protected fetchRandomImage(country: string): void 
  {
    this.getRandomCountryImage(country).subscribe({
      next: (url: string) => { 
        this.imageUrl = url;
        console.log("Image URL:", url);
        this.isLoading = false;
      },
      error: (err) => { 
        console.error("Error fetching image:", err.message);
        this.imageUrl = '';
        this.isLoading = false;
      }
    });
  }

  protected getRandomCountryImage(country: string): Observable<string> {
    
    // https://commons.wikimedia.org/w/api.php?
    // action=query
    // &generator=images
    // &prop=imageinfo
    // &gimlimit=500
    // &redirects=1
    // &titles=Cat
    // &iiprop=timestamp|user|userid|comment|canonicaltitle|url|size|dimensions|sha1|mime|thumbmime|mediatype|bitdepth
    // iiprop includes some of imageinfo properties 
    const apiUrl = 'https://commons.wikimedia.org/w/api.php';
    const params = {
      action: 'query',
      generator: 'images',
      prop: 'imageinfo',
      gimlimit: 10,
      redirects: 1,
      titles: `${country}`,
      iiprop: 'url',
      format: 'json',
      origin: '*'
    };

    return this.http.get<any>(apiUrl, { params }).pipe(
      map(response => {
        console.log("wiki resp: ", response)
        // Extract the pages object and filter results that contain images
        const pages = response.query?.pages;
        console.log("pages: ", pages)

        console.log("pages Object: ", Object.values(pages))
        const images = Object.values(pages) // convert pages to array 
          .map((page: any) => page.imageinfo?.[0]?.url) // imageinfo is an array, extract the url into new array
          .filter((url: string) => url && 
            (url.endsWith('.jpg') || url.endsWith('.jpeg') || url.endsWith('.png'))); // keep all that starts w the file extension in array

        // if images array has images, return a random, floor rounds down
        // math random is 0 - 1
        return images.length ? images[Math.floor(Math.random() * images.length)] : ''; 
        
      })
    );
  }

  protected shareTrip(): void {
    const shareUrl = `${window.location.origin}/shared-itinerary/${this.tripId}`;
    
    // Copy to clipboard
    this.clipboard.copy(shareUrl);
    
    this.snackBar.open('Share link copied to clipboard!', 'Close', { duration: 3000 });
  }
  
  protected navigateBack(): void {
    this.router.navigate(['/all-plans']);
  }


}  