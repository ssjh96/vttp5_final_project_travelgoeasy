import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './component/user/login/login.component';
import { LandingComponent } from './component/base/landing/landing.component';
import { RegisterComponent } from './component/user/register/register.component';
import { HomeComponent } from './component/base/home/home.component';
import { RouteGuardService } from './services/route-guard.service';
import { CreatePlanComponent } from './component/travel/create-plan/create-plan.component';
import { NotFoundComponent } from './component/base/not-found/not-found.component';
import { AllPlansComponent } from './component/travel/all-plans/all-plans.component';
import { ViewPlanComponent } from './component/travel/view-plan/view-plan.component';
import { UserProfileComponent } from './component/user/user-profile/user-profile.component';
import { SharedItineraryComponent } from './component/travel/shared-itinerary/shared-itinerary.component';
import { CheckoutComponent } from './component/user/payment/checkout/checkout.component';
import { PaymentSuccessComponent } from './component/user/payment/checkout/payment-success/payment-success.component';
import { PaymentCancelComponent } from './component/user/payment/checkout/payment-cancel/payment-cancel.component';



const routes: Routes = [
  {path: "", component: LandingComponent},
  {path: "Login", component: LoginComponent},
  {path: "Register", component: RegisterComponent},
  {path: "shared-itinerary/:tripId", component: SharedItineraryComponent},
  {path: "Home", component: HomeComponent, canActivate: [RouteGuardService]},
  {path: "Profile", component: UserProfileComponent, canActivate: [RouteGuardService]},
  {path: "checkout", component: CheckoutComponent, canActivate: [RouteGuardService]},
  {path: "payment-success", component: PaymentSuccessComponent, canActivate: [RouteGuardService]},
  {path: "payment-cancel", component: PaymentCancelComponent, canActivate: [RouteGuardService]},
  {path: "create-plan", component: CreatePlanComponent, canActivate: [RouteGuardService]},
  {path: "all-plans", component: AllPlansComponent, canActivate: [RouteGuardService]},
  {path: "view-plan/:tripId", component: ViewPlanComponent, canActivate: [RouteGuardService]}, 
  
  // {path: "view-plans", component: ViewPlansComponent, canActivate: [RouteGuardService]},
  // {path: "itinerary/:tripId", component: ItineraryComponent, canActivate: [RouteGuardService]},


  {path: '**', component: NotFoundComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
