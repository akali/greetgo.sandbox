import {Component, EventEmitter, Input, OnInit, Output} from "@angular/core";

@Component({
    selector: 'row',
    template: require('./row.component.html'),
    styles: [require('./row.component.css')]
})

export class RowComponent implements OnInit {
    @Input() item: any;
    @Input() displayKeys: Array<string>;
    @Input() onRemove: EventEmitter<boolean>;

    @Output() onSelect = new EventEmitter<boolean>();

    public destroied: boolean = false;
    public selected: boolean = false;

    ngOnInit(): void {

    }

    onClick() {
        console.log("row clicked: ", this.item);
    }


}
