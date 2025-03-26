import { Component, inject, Input, OnChanges, OnDestroy, OnInit, SimpleChanges, ViewChild } from '@angular/core';

import { map, Subscription } from 'rxjs';
import { MapService } from '../../../../services/map.service';
import { CountriesStore } from '../../../../store/countries.store';

@Component({
  selector: 'app-maps',
  standalone: false,
  templateUrl: './maps.component.html',
  styleUrl: './maps.component.css'
})
export class MapsComponent implements OnInit, OnChanges, OnDestroy
{  
  @Input() selectedDestination: string = ""
  
  center!: google.maps.LatLngLiteral
  markersArray: google.maps.LatLngLiteral[] = [];
  itinerarySubscription!: Subscription;
  
  private countriesStore = inject(CountriesStore);
  private mapService = inject(MapService);
  
  constructor() {}

  ngOnInit()
  {      
    this.itinerarySubscription = this.mapService.itineraryObs$.subscribe((places) => 
    {
      // pass in place[] to markersArray
      this.markersArray = places; 
      const noOfPlaces = places.length

      if(noOfPlaces > 0)
      {
        this.center = places[noOfPlaces - 1]
      }
    });
  }

  ngOnChanges(changes: SimpleChanges): void 
  {
    if(changes['selectedDestination'])
    {
      console.log("Maps - CHANGE DETECTED")
      this.loadDestination()
    }
  }

  ngOnDestroy(): void {
    this.itinerarySubscription.unsubscribe();
  }

  private loadDestination()
  {
    this.countriesStore.countries$.pipe(map((countries) => {
      return countries.find((country) => country.name === this.selectedDestination);
    })).subscribe((country) => {
      if (!country) 
      {
        // return;
        console.warn(`${this.selectedDestination} not found..`);
      }

      else 
      {
        this.center = { lat: country.lat, lng: country.lng };
        console.log(`Map centered to ${country.name} at: `, this.center);
      }
    })
  }


}
