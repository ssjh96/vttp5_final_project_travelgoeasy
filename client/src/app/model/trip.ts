export interface Trip
{
    tripId?: number, // optional property, nothing entered = undefined
    userId: number,
    tripName: string,
    destination: string,
    startDate: string,
    endDate: string,
    tripMates: string 
}