import { Component, inject, OnInit } from '@angular/core';
import { AbstractControl, FormBuilder, FormControl, FormGroup, ValidationErrors, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { NewUser } from '../../../model/user';
import { AuthenticationService } from '../../../services/authentication.service';
import { EmailService } from '../../../services/email.service';

// custom validation - passwordMatchValidator
const passwordMatchValidator = (ctrl: AbstractControl) =>
{
  const password = ctrl.get('password')?.value;
  const confirmPassword = ctrl.get('confirmPassword')?.value;

  // console.log("password: ", password);
  // console.log("confirmPassword: ", confirmPassword);
  // console.log("result: ", password === confirmPassword ? null : { mismatch: true });

  if (password === confirmPassword) {
    return null;
  }
  return { mismatch: true } as ValidationErrors;
}

@Component({
  selector: 'app-register',
  standalone: false,
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent implements OnInit
{
  
  resgistrationForm!: FormGroup;

  private fb = inject(FormBuilder);
  private router = inject(Router);
  private authService = inject(AuthenticationService);
  private emailService = inject(EmailService);

  ngOnInit(): void {
    this.resgistrationForm = this.createForm();

  }

  protected createForm(): FormGroup
  {
    return this.fb.group({
      username: this.fb.control<string>('', [Validators.required, Validators.minLength(6)]),
      email: this.fb.control<string>('', [Validators.required, Validators.email]),
      password: this.fb.control<string>('', [Validators.required, Validators.minLength(8)]),
      confirmPassword: this.fb.control<string>('', [Validators.required, Validators.minLength(8)])
    },
    {
      // check password match
      validators: passwordMatchValidator // form-level validation
    });
  }
  
  protected onRegister(): void
  {
    // console.log(this.resgistrationForm.value);
    if(this.resgistrationForm.valid)
    {
      const newUser: NewUser = this.resgistrationForm.value as NewUser;
      this.authService.register(newUser)
        .then(() => {
          this.emailService.sendWelcomeEmail(newUser.email, newUser.username).subscribe({
            next: () => console.log("Registration email sent succesfully!"),
            error: (err) => console.error('Error: ', err)
          });

          alert("Registration successful!");
          this.router.navigate(['/Login']);
        })
        .catch(err => {
          console.error("error: ", err)
          alert(err.message);
        });
    } else {
      // Mark all fields as touched to display validation messages
      this.resgistrationForm.markAllAsTouched();
    }
  }

  // Validations
  protected touchedAndInvalid(ctrlName: string): boolean
  {
    const ctrl = this.resgistrationForm.get(ctrlName) as FormControl
    return ctrl.touched && ctrl.invalid
  }

  protected isValid(ctrlName: string): boolean
  {
    return !!this.resgistrationForm.get(ctrlName)?.valid
  }
}
