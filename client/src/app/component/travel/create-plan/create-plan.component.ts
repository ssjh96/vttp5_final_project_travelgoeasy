import { Component, inject, OnInit } from '@angular/core';
import { AbstractControl, FormBuilder, FormControl, FormGroup, ValidationErrors, Validators } from '@angular/forms';
import { TripService } from '../../../services/trip.service';
import { Router } from '@angular/router';
import { AuthenticationService } from '../../../services/authentication.service';
import { Observable } from 'rxjs';
import { Country } from '../../../model/country';
import { CountriesStore } from '../../../store/countries.store';
import { EmailService } from '../../../services/email.service';
import { MatSnackBar } from '@angular/material/snack-bar';


const futureOrPresent = (ctrl: AbstractControl) => 
{
  const inputDate = new Date(ctrl.value).getTime(); 
  const today = new Date().setHours(0, 0, 0, 0);  // Start of today
  
  if(inputDate >= today)
  {
    return null; // No error
  }
  
  return {futureOrPresent: true} as ValidationErrors;
};

@Component({
  selector: 'app-create-plan',
  standalone: false,
  templateUrl: './create-plan.component.html',
  styleUrl: './create-plan.component.css'
})
export class CreatePlanComponent implements OnInit
{
  private fb = inject(FormBuilder);
  private tripService = inject(TripService);
  private router = inject(Router);
  private authSvc = inject(AuthenticationService);
  private emailSvc = inject(EmailService);
  private snackBar = inject(MatSnackBar);

  tripForm!: FormGroup;
  userId!: number;
  tripDuration: number = 0;
  showEmailHelper: boolean = false;
  invalidEmails: string[] = [];

  private countriesStore = inject(CountriesStore);
  countriesList$!: Observable<Country[]>;

  ngOnInit(): void 
  {
    this.loadCountries();
    this.tripForm = this.createTripForm();
    this.userId = this.authSvc.getUserDetails().userId;
    
    // .valueChanges is an observable for form controls
    // observe for date changes to calculate duration
    this.tripForm.get('startDate')?.valueChanges.subscribe(() => this.calculateDuration());
    this.tripForm.get('endDate')?.valueChanges.subscribe(() => this.calculateDuration());
    
    // observer tripMates for validation
    this.tripForm.get('tripMates')?.valueChanges.subscribe(val => {
      if (val) {
        this.validateEmails(val);
        this.showEmailHelper = true;
      } else {
        this.showEmailHelper = false;
        this.invalidEmails = [];
      }
    });
  }

  private loadCountries()
  {
    this.countriesList$ = this.countriesStore.countries$;
    this.countriesStore.loadCountries();
  }

  protected createTripForm(): FormGroup
  {
    return this.fb.group({
      tripName: this.fb.control<string>('', [Validators.required]),
      destination: this.fb.control<string>('', [Validators.required]),
      startDate: this.fb.control<string>('', [Validators.required, futureOrPresent]),
      endDate: this.fb.control<string>('', [Validators.required, futureOrPresent]),
      tripMates: this.fb.control<string>('')  
    });
  }

  protected onCreateTrip()
  {
    if(this.tripForm.valid && !this.hasInvalidEmails())
    {
      const tripData = {userId: this.userId, ...this.tripForm.value};
      // console.log(tripData);

      this.tripService.createTrip(tripData).subscribe(
      {
        next: (tripId) => 
        {
          console.log('Trip created with ID: ', tripId);
          
          // Send trip invitations to tripmates if any
          if(tripData.tripMates && tripData.tripMates.trim() !== '')
          {
            const tripMateEmails = tripData.tripMates.split(',')
              .map((email: string) => email.trim())
              .filter((email: string) => this.isValidEmail(email));
            
            if (tripMateEmails.length > 0) {
              this.snackBar.open(`Sending invitations to ${tripMateEmails.length} companions...`, 'Close', { duration: 3000 });
            }
          }

          // send successful trip creation email
          const userDetails = this.authSvc.getUserDetails();
          this.emailSvc.sendTripCreationEmail(userDetails.email, 
                                              userDetails.username, 
                                              tripData.tripName, 
                                              tripData.destination, 
                                              tripData.startDate,
                                              tripData.endDate,
                                              tripId)
                                            .subscribe(
                                            {
                                              next: () => { console.log("Trip creation email sent successfully") },
                                              error: (err) => console.error('Error sending trip creation email: ', err)
                                            });
          
          // Send invitations to tripmates (if any)
          if(tripData.tripMates && tripData.tripMates.trim() !== '')
          {
            const tripMateEmails = tripData.tripMates.split(',').map((email: string) => email.trim());
            
            console.log("tripMateEmails: ", tripMateEmails);
            this.snackBar.open(`Sending invitations to ${tripMateEmails.length} companions...`, 'Close', { duration: 3000 });
            
            // Send invitation email to each valid email address
            tripMateEmails.forEach((email: string) => {
              this.emailSvc.sendTripInvitationEmail(
                email, // target email
                userDetails.username, // inviter name
                tripData.tripName,
                tripData.destination,
                tripData.startDate,
                tripData.endDate,
                tripId)
                .subscribe({
                  next: () => console.log(`Invitation sent to ${email}`),
                  error: (err) => console.error(`Error sending invitation to ${email}:`, err)
                });
            });
          }
          else 
          {
            console.log("No tripmates");
          }
          
          this.snackBar.open('Trip successfully created!', 'Close', { duration: 3000 });
          
          this.router.navigate([`/view-plan/${tripId}`]);
        },
        error: (err) => 
        {
          this.snackBar.open(`Error: ${err.message}`, 'Close', { duration: 5000 });
        }
      });     
    }
  }
  
  private calculateDuration(): void 
  {
    const startDateVal = this.tripForm.get('startDate')?.value;
    const endDateVal = this.tripForm.get('endDate')?.value;
    
    if (startDateVal && endDateVal) {
      const startDate = new Date(startDateVal);
      const endDate = new Date(endDateVal);
      
      // calculate the diff in days
      const timeDiff = endDate.getTime() - startDate.getTime();
      const daysDiff = Math.ceil(timeDiff / (1000 * 3600 * 24)) + 1; // +1 to include both start and end days
      
      this.tripDuration = daysDiff > 0 ? daysDiff : 0;
    } else {
      this.tripDuration = 0;
    }
  }
  


  // VALIDATIONS
  private validateEmails(emailsString: string): void 
  {
    if (!emailsString || emailsString.trim() === '') 
    {
      this.invalidEmails = [];
      return;
    }
    
    const emails = emailsString.split(',').map(email => email.trim());
    this.invalidEmails = emails.filter(email => email !== '' && !this.isValidEmail(email));
  }
  
  protected hasInvalidEmails(): boolean 
  {
    return this.invalidEmails.length > 0;
  }

  protected touchedAndInvalid(ctrlName: string): boolean
  {
    const ctrl = this.tripForm.get(ctrlName) as FormControl;
    return ctrl.touched && ctrl.invalid;
  }

  protected isValid(ctrlName: string): boolean
  {
    return !!this.tripForm.get(ctrlName)?.valid;
  }

  private isValidEmail(email: string): boolean 
  {
    const emailPattern = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$/;
    return emailPattern.test(email);
  }
}
