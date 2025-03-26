import { Component, inject, OnInit } from '@angular/core';
import { Country } from './model/country';
import { CountriesService } from './services/countries.service';
import { CountriesStore } from './store/countries.store';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  standalone: false,
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit
{
  title = 'client';
  countries!: Country[];
  private countriesSvc = inject(CountriesService);
  private countriesStore = inject(CountriesStore);

  async ngOnInit() 
  {
    // load all countries from server api
    this.countries = await this.countriesSvc.getAllCountries();

    // add each country to state and store
    this.countries.forEach((countryObj) => {
      // console.log("countryObj: ", countryObj);
      // console.log("country name: ", countryObj.name);
      this.countriesStore.addNewCountry(countryObj);
    });
  }
  
}
