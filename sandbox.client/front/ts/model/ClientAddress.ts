import {AddressType} from "./AddressType";

export class ClientAddress {
    public client: number /* int */;
    public type: AddressType;
    public street: string;
    public house: string;
    public flat: string;
}
