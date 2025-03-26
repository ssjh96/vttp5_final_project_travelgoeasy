import { Component, inject, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthenticationService } from '../../../../../services/authentication.service';
import { PaymentService } from '../../../../../services/payment.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { EmailService } from '../../../../../services/email.service';
import { UserDetails } from '../../../../../model/user';

@Component({
  selector: 'app-payment-success',
  standalone: false,
  templateUrl: './payment-success.component.html',
  styleUrl: './payment-success.component.css'
})
export class PaymentSuccessComponent implements OnInit
{
  private router = inject(Router);
  private authSvc = inject(AuthenticationService);
  private paymentSvc = inject(PaymentService);
  private emailSvc = inject(EmailService);
  private snackBar = inject(MatSnackBar);

  username: string = '';
  errorMessage: string = '';
  upgradeComplete: boolean = false;

  ngOnInit(): void {
    const userDetails: UserDetails = this.authSvc.getUserDetails();
    this.username = userDetails.username;
    this.upgradeUserToPro();

    this.emailSvc.sendPaymentSuccessEmail(userDetails.email, this.username).subscribe({
      next: () => console.log("payment success email sent succesfully!"),
      error: (err) => console.error('Error: ', err)
    });
    
  }

  private upgradeUserToPro(): void
  {
    this.paymentSvc.upgradeUserToPro(this.username).subscribe({
      next: (response) => {
        console.log('Upgrade response: ', response);
        this.upgradeComplete = true;
        this.snackBar.open('Succesfully upgraded to PRO!', 'Dismiss', {duration: 5000})
      },
      error: (err) => {
        console.error('Error upgrading user: ', err);
        this.errorMessage = err.message;
        this.snackBar.open('Error upgrading to PRO..', 'Dismiss', {duration: 5000})
      }
    })
  }

  protected navigateToHome(): void {
    this.router.navigate(['/Home']);
  }

  protected logout(): void 
  {
    this.authSvc.logout();
    this.router.navigate(['/Login']);
  }

}
