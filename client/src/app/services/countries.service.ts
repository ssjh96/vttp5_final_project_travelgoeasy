import { HttpClient, HttpHeaders } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Country } from '../model/country';
import { lastValueFrom } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class CountriesService {

  constructor() { }

  private http = inject(HttpClient)
  
  public getAllCountries(): Promise<Country[]>
  {
    return lastValueFrom(this.http.get<Country[]>('api/countries/all'));
  }
}
