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
  
  center: google.maps.LatLngLiteral = {lat: 0, lng: 0};
  markersArray: google.maps.LatLngLiteral[] = [];
  itinerarySubscription!: Subscription;
  zoom: number = 5;
  
  private countriesStore = inject(CountriesStore);
  private mapService = inject(MapService);
  
  constructor() {}

  ngOnInit()
  { 
    this.zoom = 5;     
    this.itinerarySubscription = this.mapService.itineraryObs$.subscribe((places) => 
    {
      // pass in place[] to markersArray
      this.markersArray = places; 
      const noOfPlaces = places.length
      // this.center = { lat: places[0].lat, lng: places[0].lng };
      
      if(noOfPlaces > 0)
      {
        // this.center = places[noOfPlaces - 1]
        this.center = { 
          lat: places[noOfPlaces - 1].lat, 
          lng: places[noOfPlaces - 1].lng 
        };
        this.zoom = 10;
      }
    });
    if (this.selectedDestination) 
    {
      this.loadDestination();
    }
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
    this.triggerMapRefresh(); // trigger after center is set
  }

  private triggerMapRefresh() 
  {
    this.center = { ...this.center };
  }

}


