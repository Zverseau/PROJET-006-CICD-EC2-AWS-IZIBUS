import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CompagniesListComponent } from './compagnies-list.component';

describe('CompagniesListComponent', () => {
  let component: CompagniesListComponent;
  let fixture: ComponentFixture<CompagniesListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CompagniesListComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CompagniesListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
