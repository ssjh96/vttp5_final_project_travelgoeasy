import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { AuthenticationService } from '../../../services/authentication.service';
import { Router } from '@angular/router';
import { JwtRequest } from '../../../model/jwt-request';


@Component({
  selector: 'app-login',
  standalone: false,
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent implements OnInit
{
  loginForm!: FormGroup;
  private fb = inject(FormBuilder);
  private router = inject(Router);
  private authService = inject(AuthenticationService);
  // private jwtStore = inject(JwtStore);

  
  ngOnInit(): void 
  {
    this.loginForm = this.createForm();
    // this.jwtStore.loadJwt(); // load jwt from indexdb
  } 

  // Form creation
  protected createForm(): FormGroup
  {
    return this.fb.group({
      username: this.fb.control<string>('', [Validators.required, Validators.minLength(6)]),
      password: this.fb.control<string>('', [Validators.required, Validators.minLength(8)])
    });
  }

  // Form submission
  protected onLogin(): void
  {
    console.log(this.loginForm.value);
    if(this.loginForm.valid)
    {
      const jwtRequest: JwtRequest = this.loginForm.value as JwtRequest;
      this.authService.login(jwtRequest)
        .then(() => {
          console.log("Login - login successful");
          this.router.navigate(['/Home']);
        })
        .catch(err => {
          console.error("Login - error: ", err)
          alert(err.message);
          this.loginForm.reset();
        });
      }
    }


  

  // Validations
  protected touchedAndInvalid(ctrlName: string): boolean
  {
    const ctrl = this.loginForm.get(ctrlName) as FormControl
    return ctrl.touched && ctrl.invalid
  }

  protected isValid(ctrlName: string): boolean
  {
    return !!this.loginForm.get(ctrlName)?.valid
  }


}
