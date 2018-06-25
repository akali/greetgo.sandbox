import {AddressType} from "./AddressType";

export class ClientAddress {
  constructor(client: number, type: AddressType, street: string, house: string, flat: string) {
    this.client = client;
    this.type = type;
    this.street = street;
    this.house = house;
    this.flat = flat;
  }

  public client: number /* int */;
  public type: AddressType;
  public street: string;
  public house: string;
  public flat: string;
}
