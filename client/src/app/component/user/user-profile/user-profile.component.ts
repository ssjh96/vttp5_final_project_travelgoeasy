import { Component, ElementRef, inject, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthenticationService } from '../../../services/authentication.service';
import { ProfileService } from '../../../services/profile.service';
import { Profile } from '../../../model/profile';
import { RankData } from '../../../model/rank';

@Component({
  selector: 'app-user-profile',
  standalone: false,
  templateUrl: './user-profile.component.html',
  styleUrl: './user-profile.component.css'
})
export class UserProfileComponent implements OnInit
{
  private fb = inject(FormBuilder);
  private authSvc = inject(AuthenticationService);
  private profileSvc = inject(ProfileService);

  profileForm!: FormGroup;

  // user data
  userId!: number;
  username: string = '';
  profile: Profile = {userId: 0};
  rankData: RankData = {rank: '', tripCount: 0};

  // profile pic
  selectedFile: File | null = null; // initial no file
  profilePicUrl: string | null = null; // initial no profile pic

  // success & error
  errorMessage: string = '';
  successMessage: string = '';

  ngOnInit(): void {
    this.profileForm = this.createForm();
    this.loadUserData(); // gets userDetails (id and username) -> updates profilePicUrl (either null or got pic) -> patch firstName lastName if present
  }

  private createForm(): FormGroup
  {
    return this.fb.group({
      firstName: this.fb.control<string>('', [Validators.required]),
      lastName: this.fb.control<string>('', [Validators.required])
    })
  }

  private loadUserData(): void
  {
    const userDetails = this.authSvc.getUserDetails();
    this.userId = userDetails.userId;
    this.username = userDetails.username;

    console.log(this.userId)

    // userId must updated before the methods
    this.loadProfile(); 
    this.loadRank();
  }

  // Get user profile from server
  private loadProfile(): void 
  {
    this.errorMessage = '';
    
    this.profileSvc.getProfile(this.userId).subscribe({
      next: (data) => {
        // const jsonData = JSON.parse(jsonString)
        // Parse JSON if the response is a string
        // console.log("type: ", typeof response)
        // const data = typeof response === 'string' ? JSON.parse(response) : response;

        // Data is actually already an object
        console.log("Data: ", data)
        this.profile = data;
        this.profilePicUrl = data.profilePicture 

        // Update form with data from server
        if(data.firstName && data.lastName)
        {
          this.profileForm.patchValue({
            firstName: data.firstName,
            lastName: data.lastName
          })
        }
      },
      error: (err) => {
        console.error('Error loading profile:', err);
        this.errorMessage = 'Failed to load profile. Please try again.';
      }
    })
  }

  private loadRank(): void
  {
    this.profileSvc.getUserRank(this.userId).subscribe({
      next: (data) => {
        this.rankData = data; // map 
      },
      error: (err) => {
        console.error('Error loading profile rank:', err);
        this.errorMessage = 'Failed to load rank. Please try again.';
      }
    })
  }

  
  
  protected onFileSelected(event: Event){
    console.log('onFileSelected')
    const input = event.target as HTMLInputElement
    // the target is the file?
    console.log('event: ', event)
    console.log('input: ', input)

    if(input.files && input.files.length > 0)
    {
      this.selectedFile = input.files[0]; // retrieve 1 file 
      console.log(this.selectedFile);

      // create preview
      const reader = new FileReader();
      reader.onload = () => {
        this.profilePicUrl = reader.result as string;
      };
      reader.readAsDataURL(this.selectedFile);
      // console.log('dataUri: ', this.dataUri); // processing on the reader havent finish, undefined, we processing and reading at the same time, we can print in the onload and youll get it
    }
  }

  protected saveChanges()
  {
    // reset messages
    this.errorMessage = '';
    this.successMessage = '';

    const firstName = this.profileForm.get('firstName')?.value;
    const lastName = this.profileForm.get('lastName')?.value;

    let updateReq;
    if(this.selectedFile)
    {
      updateReq = this.profileSvc.updateProfile(this.userId, firstName, lastName, this.selectedFile)
    }
    else
    {
      updateReq = this.profileSvc.updateProfileNoPic(this.userId, firstName, lastName)
    }

    updateReq.subscribe({
      next: (data) => {
        // const jsonData = JSON.parse(response);

        if (data.success) // json key "success" exist
        {
          this.successMessage = 'Profile updated successfully!'
          this.loadProfile(); // reload to show updated profile
        }
        else if(data.error) // json key "error" exist
        {
          this.errorMessage = data.error; // Cannot update user profile in json data
        }
      },
      error: (err) => {
        console.error('Error updating profile:', err);
        this.errorMessage = 'Failed to update profile. Please try again.';
      }
    })
  }

  @ViewChild('fileInput') fileInput!: ElementRef;
  protected removeProfilePic(): void
  {
    if(confirm("Are you sure you want to remove your profile pic?"))
    {
      this.errorMessage = '';
      this.successMessage = '';

      this.profileSvc.removeProfilePic(this.userId).subscribe({
        next: (data) => {
          // const jsonData = JSON.parse(response);
          
          if (data.success) // json key "success" exist
          {
            this.successMessage = 'Profile pic deleted successfully!'
            this.profilePicUrl = null;
            this.selectedFile = null;
            if (this.fileInput) {
              this.fileInput.nativeElement.value = '';
            }
            this.loadProfile(); // reload to show updated profile
          }
          else if(data.error) // json key "error" exist
          {
            this.errorMessage = data.error; // Cannot update user profile in json data
          }
        },
        error: (err) => {
          console.error('Error deleting profile pic:', err);
          this.errorMessage = 'Failed to remove profile picture. Please try again.';
        }
        
      })
    }
  }

}
