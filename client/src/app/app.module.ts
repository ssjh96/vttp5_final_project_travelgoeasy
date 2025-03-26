import { NgModule, isDevMode } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { GoogleMapsModule } from '@angular/google-maps';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HeaderComponent } from './component/base/header/header.component';
import { FooterComponent } from './component/base/footer/footer.component';
import { LoginComponent } from './component/user/login/login.component';
import { LandingComponent } from './component/base/landing/landing.component';
import { RegisterComponent } from './component/user/register/register.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HomeComponent } from './component/base/home/home.component';
import { provideHttpClient } from '@angular/common/http';
import { UserProfileComponent } from './component/user/user-profile/user-profile.component';
import { CreatePlanComponent } from './component/travel/create-plan/create-plan.component';
import { AllPlansComponent } from './component/travel/all-plans/all-plans.component';
import { NotFoundComponent } from './component/base/not-found/not-found.component';
import { ServiceWorkerModule } from '@angular/service-worker';
import { MaterialModule } from './material/material.module';
import { ViewPlanComponent } from './component/travel/view-plan/view-plan.component';
import { ItineraryComponent } from './component/travel/view-plan/itinerary/itinerary.component';
import { MapsComponent } from './component/travel/view-plan/maps/maps.component';
import { SharedItineraryComponent } from './component/travel/shared-itinerary/shared-itinerary.component';
import { CheckoutComponent } from './component/user/payment/checkout/checkout.component';

import { PaymentCancelComponent } from './component/user/payment/checkout/payment-cancel/payment-cancel.component'; 
import { PaymentSuccessComponent } from './component/user/payment/checkout/payment-success/payment-success.component';




@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    FooterComponent,
    LoginComponent,
    LandingComponent,
    RegisterComponent,
    HomeComponent,
    UserProfileComponent,
    CreatePlanComponent,
    NotFoundComponent,
    ItineraryComponent,
    ViewPlanComponent,
    MapsComponent,
    AllPlansComponent,
    SharedItineraryComponent,
    CheckoutComponent,
    PaymentSuccessComponent,
    PaymentCancelComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    ReactiveFormsModule,
    MaterialModule,
    FormsModule,
    GoogleMapsModule,
    ServiceWorkerModule.register('ngsw-worker.js', {
      enabled: !isDevMode(),
      // Register the ServiceWorker as soon as the application is stable
      // or after 30 seconds (whichever comes first).
      registrationStrategy: 'registerWhenStable:30000'
    })
  ],
  providers: [provideHttpClient()],
  bootstrap: [AppComponent]
})
export class AppModule { }
