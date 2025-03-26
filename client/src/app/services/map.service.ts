import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { MapPlace, Place } from '../model/place';

@Injectable({
  providedIn: 'root'
})
export class MapService 
{
  constructor() { }

  private itinerarySubject = new BehaviorSubject<MapPlace[]>([]); // initial state empty
  itineraryObs$ = this.itinerarySubject.asObservable();

  // Add a single place to the itinerary
  addPlace(place: MapPlace) {
    console.log('Adding place to itinerary:', place);
    const currentPlaces = this.itinerarySubject.getValue();
    const updatedPlaces = [...currentPlaces, place];
    this.itinerarySubject.next(updatedPlaces);
  }

  // Update all places at once
  updatePlaces(places: MapPlace[]) {
    console.log('Updating places in itinerary:', places);
    this.itinerarySubject.next(places);
  }

  // Clear all places
  clearPlaces() {
    console.log('Clearing all places from itinerary');
    this.itinerarySubject.next([]);
  }

  // Convert places from backend format to map format
  convertFromBackend(places: Place[]): MapPlace[] {
    return places.map(place => ({
      lat: place.latitude,
      lng: place.longitude,
      day: place.dayNumber,
      name: place.placeName,
      address: place.address
    }));
  }

  // Convert to backend format for saving
  convertToBackendFormat(places: any[], tripId: number): Place[] {
    return places.map((place, index) => {
      // For each day, find all places in that day
      const dayPlaces = places.filter(p => p.day === place.day);
      // Find this place's position within its day
      const orderInDay = dayPlaces.findIndex(p => p.id === place.id);
      
      // Create a Place object that matches the backend model
      return {
        tripId: tripId,
        placeName: place.name || 'Unnamed Place',
        address: place.address || 'No address available',
        latitude: place.lat,
        longitude: place.lng,
        dayNumber: place.day,
        orderIndex: orderInDay >= 0 ? orderInDay : 0
      };
    });
  }
}
